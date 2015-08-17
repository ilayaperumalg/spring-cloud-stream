/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.cloud.stream.annotation.EnableModule;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.Processor;
import org.springframework.cloud.stream.annotation.Sink;
import org.springframework.cloud.stream.annotation.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.util.MultiValueMap;

/**
 * @author Ilayaperumal Gopinathan
 */
public class ChannelBindingImportSelector implements ImportSelector {

	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		MultiValueMap<String, Object> attributes =
				importingClassMetadata.getAllAnnotationAttributes(EnableModule.class.getName(), false);
		Set<String> configurations = new HashSet<>();
		configurations.add(ChannelBindingAdapterConfiguration.class.getName());
		configurations.add(ChannelBindingAdapterRunner.class.getName());
		for (Class<?> type: collectClasses(attributes.get("value"))) {
			if (type.getName().equals(Processor.class.getName())) {
				configurations.add(ProcessorConfig.class.getName());
			}
			if (type.getName().equals(Sink.class.getName())) {
				configurations.add(SinkConfig.class.getName());
			}
			else if (type.getName().equals(Source.class.getName())) {
				configurations.add(SourceConfig.class.getName());
			}
		}
		return configurations.toArray(new String[0]);
	}

	@Configuration
	private static class SinkConfig {

		public SinkConfig() {}
		@Bean(name="input")
		SubscribableChannel inputChannel() {
			return new DirectChannel();
		}

		@Bean
		Sink sink() {
			return new Sink() {
				@Override
				@Input
				public SubscribableChannel input() {
					return inputChannel();
				}
			};
		}
	}

	@Configuration
	private static class SourceConfig {

		public SourceConfig() {}
		@Bean(name="output")
		MessageChannel outputChannel() {
			return new DirectChannel();
		}

		@Bean
		Source source() {
			return new Source() {
				@Override
				@Output
				public MessageChannel output() {
					return outputChannel();
				}
			};
		}
	}

	@Configuration
	private static class ProcessorConfig {

		public ProcessorConfig() {}
		@Bean(name="output")
		MessageChannel outputChannel() {
			return new DirectChannel();
		}

		@Bean(name="input")
		SubscribableChannel inputChannel() {
			return new DirectChannel();
		}

		@Bean
		Processor processor() {
			return new Processor() {
				@Override
				@Input
				public SubscribableChannel input() {
					return inputChannel();
				}

				@Override
				@Output
				public MessageChannel output() {
					return outputChannel();
				}
			};
		}
	}

	private List<Class<?>> collectClasses(List<Object> list) {
		ArrayList<Class<?>> result = new ArrayList<Class<?>>();
		for (Object object : list) {
			for (Object value : (Object[]) object) {
				if (value instanceof Class && void.class != value) {
					result.add((Class<?>) value);
				}
			}
		}
		return result;
	}
}
