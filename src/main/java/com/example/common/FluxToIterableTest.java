package com.example.common;

import com.azure.cosmos.implementation.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.*;

public class FluxToIterableTest {

    private static Logger logger = LoggerFactory.getLogger(FluxToIterableTest.class);

    public static void main(String[] args) {
        Integer integer1 = Flux.range(0, 10)
                .flatMap(integer -> {
                    logger.info("Next item is : {} ", integer);
                    return Flux.just(integer);
                })
                .blockLast();

        logger.info("last integer is : {}", integer1);

        Iterable<Integer> iterable = Flux.range(0, 10)
                .flatMap(integer -> {
                    logger.info("Next item is : {} ", integer);
                    return Flux.just(integer);
                })
                .toIterable();

        for (Integer integer : iterable) {
            logger.info("Next element is :{}", integer);
        }

        Iterator<Integer> iterator = iterable.iterator();
        Collections.singletonList(iterator);

    }
}
