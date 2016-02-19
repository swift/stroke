/*
 * Copyright (c) 2010-2016, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.network;

import com.ibm.icu.util.BytesTrie.Iterator;
import com.isode.stroke.base.RandomGenerator;
import com.isode.stroke.signals.Signal1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class DomainNameServiceQuery {

    public static class Result {

        public Result() {
            hostname = "";
            port = -1;
            priority = -1;
            weight = -1;
        }

        public Result(String hostname, int port, int priority, int weight) {
            this.hostname = hostname;
            this.port = port;
            this.priority = priority;
            this.weight = weight;
        }
        public final String hostname;
        public final int port;
        public final int priority;
        public final int weight;
    };

    /**
     * Compares {@link DomainNameServiceQuery.Result} based on their 
     * priority only.
     *
     */
    public final static class ResultPriorityComparator implements Comparator<Result> {

        public int compare(DomainNameServiceQuery.Result a, DomainNameServiceQuery.Result b) {
            if (a.priority < b.priority) {
                return -1;
            }
            if (a.priority > b.priority) {
                return 1;
            }
            return 0;
        }
    };

    public abstract void run();
    public final Signal1<Collection<Result>> onResult = new Signal1<Collection<Result>>();
    
    public static void sortResults(List<Result> queries,RandomGenerator generator) {
        ResultPriorityComparator comparator = new ResultPriorityComparator();
        Collections.sort(queries,comparator);
        int i = 0;
        while (i < queries.size()) {
            Result current = queries.get(i);
            int nextI = upperBound(queries, current, comparator);
            if ((nextI - i) > 1) {
                List<Result> subList = queries.subList(i, nextI);
                List<Integer> weights = transform(subList, new UnaryOperation<Result, Integer>() {

                    @Override
                    public Integer perform(Result item) {
                        // easy hack to account for '0' weights getting at least some weight
                        return Integer.valueOf(item.weight+1);
                    }

                });
                for (int j = 0; j < (weights.size()-1); j++) {
                    List<Integer> cumulativeWeights =
                            partialSum(weights.subList(j, weights.size()), Integer.valueOf(0), 
                                    new BinaryOperation<Integer, Integer, Integer>() {

                                @Override
                                public Integer perform(Integer int1,
                                        Integer int2) {
                                    int results = int1.intValue() + int2.intValue();
                                    return Integer.valueOf(results);
                                }
                                
                            });
                    int lastWeight = cumulativeWeights.get(cumulativeWeights.size() - 1).intValue();
                    int randomNumber = generator.generateRandomInteger(lastWeight);
                    int selectedIndex = lowerBound(cumulativeWeights, Integer.valueOf(randomNumber));
                    Collections.swap(subList, j, j+selectedIndex);
                    Collections.swap(weights, j, j+selectedIndex);
                }
                
            }
            i = nextI;
        }
    }
    
    /**
     * Behaves similarly to the c++ function std::upper_bound.
     * Given a range that is sorted with respect to the given
     * {@link Comparator}, returns the index of the first element in the range greater
     * (under the {@link Comparator}) then the given value.
     * @param <T> The type of the value.
     * @param range A sorted (with respect to {@code comp}) list of
     * T.  Should not be {@code null}.
     * @param value A value to search the list for.  Should not be {@code null}.
     * @param comp The comparator to use for the search, the {@code range} should be
     * sorted with respect to this.  Should not be {@code null}.
     * @return The index of the first element in {@code range} that is greater (under
     * {@code comp}) then {@code value}, or {@code range.size()} if there is no such value.
     */
    public static <T> int upperBound(List<? extends T> range,T value,Comparator<? super T> comp) {
        for (int i = 0; i < range.size(); i++) {
            T current = range.get(i);
            if (comp.compare(current, value) > 0) {
                return i;
            }
        }
        return range.size();
    }
    
    /**
     * Behaves similarly to the c++ function std::upper_bound.
     * Given a range that is sorted with respect to the natural order of its elements
     * , returns the index of the first element in the range greater then the given value.
     * @param <T> The type of the value.
     * @param range A sorted list of
     * T.  Should not be {@code null}.
     * @param value A value to search the list for.  Should not be {@code null}.
     * @return The index of the first element in {@code range} that is greate then {@code value},
     * or {@code range.size()} if there is no such value.
     */
    public static <T extends Comparable<T>> int upperBound(List<? extends T> range,T value) {
        Comparator<T> comparator = new Comparator<T>() {

            @Override
            public int compare(T a, T b) {
                return a.compareTo(b);
            }
        };
        return upperBound(range, value, comparator);
    }
    
    /**
     * Behaves similarly to the c++ function std::lower_bound.
     * Given a range that is sorted with respect to the given
     * {@link Comparator}, returns the index of the first element in the range that is not
     * less then (i.e it is greater then or equal to) a given value.
     * @param <T> The type of the value.
     * @param range A sorted (with respect to {@code comp}) list of
     * T.  Should not be {@code null}.
     * @param value A value to search the list for.  Should not be {@code null}.
     * @param comp The comparator to use for the search, the {@code range} should be
     * sorted with respect to this.  Should not be {@code null}.
     * @return The index of the first element in {@code range} that is not less then 
     * (i.e it is greater then or equal to) then {@code value}, or {@code range.size()} 
     * if there is no such value.
     */
    public static <T> int lowerBound(List<? extends T> range,T value,Comparator<? super T> comp) {
        for (int i = 0; i < range.size(); i++) {
            T current = range.get(i);
            if (comp.compare(current, value) < 0) {
                continue;
            }
            return i;
        }
        return range.size();
    }
    
    /**
     * Behaves similarly to the c++ function std::lower_bound.
     * Given a range that is sorted with respect to the natural order of its elements
     * , returns the index of the first element in the range that is not less then
     * (i.e it is greater then or equal to) a given value.
     * @param <T> The type of the value.
     * @param range A sorted list of
     * T.  Should not be {@code null}.
     * @param value A value to search the list for.  Should not be {@code null}.
     * @return The index of the first element in {@code range} that is not less then
     * (i.e it is greater then or equal to) {@code value},
     * or {@code range.size()} if there is no such value.
     */
    public static <T extends Comparable<T>> int lowerBound(List<? extends T> range,T value) {
        Comparator<T> comparator = new Comparator<T>() {

            @Override
            public int compare(T a, T b) {
                return a.compareTo(b);
            }
        };
        return lowerBound(range, value, comparator);
    }
    
    /**
     * Operation that produces an item of type T from
     * an item of type S
     * @param <S> Type of input to the operation
     * @param <T> Type of output of the operation
     */
    public interface UnaryOperation<S,T> {
        
        /**
         * Produces an item of type T from an item of type S
         * @param item An item of type S.
         * @return An item of type T
         */
        public T perform(S item);
        
    }
    
    /**
     * Operation that produces an item of type T from
     * an items of type R and an item of type S.
     *
     * @param <R> Type of the first input to the operation.
     * @param <S> Type of the second input to the operation.
     * @param <T> Type of the output of the operation.
     */
    public interface BinaryOperation<R,S,T> {
        
        /**
         * Produces an item of type from an Item of type R
         * and an Item of type S
         * @param input1 An item of type R
         * @param input2 An item of type S
         * @return An item of type T
         */
        public T perform(R input1,S input2);
        
    }
    
    /**
     * Behaves similar to the C++ function std::transform
     * Transform a list of items of type T into a list of item of
     * type S using the given {@link UnaryOperation}
     * @param <S> Type of the input objects
     * @param <T> Type of the output objects.
     * @param input The input objects to apply the {@link UnaryOperation}
     * to.
     * @param operation The {@link UnaryOperation} to apply to the input
     * to get the output.
     * @return The results of applying the {@link UnaryOperation} to
     * the input in the order they are returned by the input's {@link Iterator}.
     */
    public static <S,T> List<T> transform(Collection<? extends S> input,
            UnaryOperation<? super S,? extends T> operation) {
        List<T> output = new ArrayList<T>(input.size());
        for (S item : input) {
            T product = operation.perform(item);
            output.add(product);
        }
        return output;
    }
    
    /**
     * Produces the partial sum of the elements in a {@link Collections} using the
     * given {@link BinaryOperation}.
     * @param <T> The type of objects to calculate the sum for
     * @param input The items to produce the partial sums for.  Should not be {@code null}.
     * @param initialValue The initial value for the sum to add the first element.
     * @param sumOp The {@link BinaryOperation} to produce the sum must take two items
     * of type T to produce another item of type T.  Should not be {@code null}.
     * @return A {@link List} of T consisting of the partial sums of the input. 
     * Will not be {@code null}
     */
    public static <T> List<T> partialSum(Collection<? extends T> input,
            T initialValue,BinaryOperation<? super T,? super T,? extends T> sumOp) {
        List<T> partialSums = new ArrayList<T>(input.size());
        T previousSum = initialValue;
        for (T currentInput : input) {
            T newSum = sumOp.perform(previousSum, currentInput);
            partialSums.add(newSum);
            previousSum = newSum;
        }
        return partialSums;
    }
    
}
