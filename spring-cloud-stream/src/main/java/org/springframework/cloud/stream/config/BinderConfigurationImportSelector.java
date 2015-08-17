package org.springframework.cloud.stream.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Ilayaperumal Gopinathan
 */
public class BinderConfigurationImportSelector implements ImportSelector {

	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return new String[] {
				ChannelBindingAdapterConfiguration.class.getName(),
				AggregateBuilderConfiguration.class.getName()
		};
	}
}
