/*
 * Copyright 2016 the original author or authors.
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
package org.springframework.cloud.stream.binding;

import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.ChannelBindingServiceProperties;
import org.springframework.integration.channel.ChannelInterceptorAware;
import org.springframework.integration.support.MessageBuilderFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.util.StringUtils;

/**
 * A {@link MessageChannelConfigurer} that adds a {@link org.springframework.messaging.support.ChannelInterceptor} to
 * the message channel to set the `ContentType` header for the message (if not already set) based on the `ContentType` binding
 * property of the channel.
 *
 * @author Ilayaperumal Gopinathan
 */
public class ContentTypeConfigurer implements MessageChannelConfigurer {

	private final ChannelBindingServiceProperties channelBindingServiceProperties;

	private final MessageBuilderFactory messageBuilderFactory;

	public ContentTypeConfigurer(ChannelBindingServiceProperties channelBindingServiceProperties,
			MessageBuilderFactory messageBuilderFactory) {
		this.channelBindingServiceProperties = channelBindingServiceProperties;
		this.messageBuilderFactory = messageBuilderFactory;
	}

	@Override
	public void configureMessageChannel(MessageChannel messageChannel, String channelName) {
		final BindingProperties bindingProperties = channelBindingServiceProperties.getBindings().get(channelName);
		if (bindingProperties != null && StringUtils.hasText(bindingProperties.getContentType())) {
			if (messageChannel instanceof ChannelInterceptorAware) {
				((ChannelInterceptorAware) messageChannel).addInterceptor(new ChannelInterceptorAdapter() {
					@Override
					public Message<?> preSend(Message<?> message, MessageChannel messageChannel) {
						Object contentType = message.getHeaders().get(MessageHeaders.CONTENT_TYPE);
						if (contentType == null) {
							contentType = bindingProperties.getContentType();
							return messageBuilderFactory
									.fromMessage(message)
									.setHeader(MessageHeaders.CONTENT_TYPE, contentType)
									.build();
						}
						return message;
					}
				});
			}
		}
	}

}
