/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.co.dalelane.kafkaprojections.utils;

import java.util.Properties;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gets the properties needed to configure Kafka clients.
 *
 *  For local development, this will use values in resources/META-INF/microprofile-config.properties
 *  For k8s deployments, this will come from environment variables provided through a Secret
 */
public class KafkaConfig {

    private static Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    public static Properties getKafkaClientConfiguration() {
        log.info("Reading Kafka config from microprofile config");
        Properties appProps = new Properties();

        Config config = ConfigProvider.getConfig();
        config.getPropertyNames().forEach(propertyName -> {
            if (propertyName.startsWith("kafka.")) {
                String kafkaKey = propertyName.replace("kafka.", "");
                appProps.put(kafkaKey, config.getValue(propertyName, String.class));
            }
        });

        return appProps;
    }
}
