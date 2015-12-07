/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.aggregation;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.stream.aggregate.AggregateApplication;
import org.springframework.cloud.stream.aggregate.SharedChannelRegistry;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.utils.MockBinderConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

/**
 * @author Marius Bogoevici
 */
public class ModuleAggregationTest {

	@Test
	public void testModuleAggregation() {
		ConfigurableApplicationContext aggregatedApplicationContext = AggregateApplication.run(TestSource.class,
				TestProcessor.class);
		SharedChannelRegistry sharedChannelRegistry = aggregatedApplicationContext.getBean(SharedChannelRegistry.class);
		assertThat(sharedChannelRegistry.getAll().keySet(), hasSize(2));
	}


	@EnableBinding(Source.class)
	@EnableAutoConfiguration
	@Import(MockBinderConfiguration.class)
	public static class TestSource {

	}

	@EnableBinding(Processor.class)
	@EnableAutoConfiguration
	@Import(MockBinderConfiguration.class)
	public static class TestProcessor {

	}

}
