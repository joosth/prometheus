/*
 * Prometheus plugin for Grails
 * Copyright 2016, Open-T B.V., and individual contributors as indicated
 * by the @author tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License
 * version 3 published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses
 */
package org.open_t.prometheus
import io.prometheus.jmx.*
import io.prometheus.client.hotspot.*
import io.prometheus.client.*
import grails.util.Holders
import javax.annotation.PostConstruct


class MetricService {

    def collectorRegistries=[:]

    def jmxCollectors=[]
    def grailsApplication

    boolean transactional = false

    @PostConstruct
    def init() {
        new io.prometheus.client.hotspot.DefaultExports().initialize();
        def jmxCollectorsList=grailsApplication.config.prometheus?.jmxcollectors?:[default:"classpath:prometheus-jmx.yml"]
        log.trace "Starting list iterator of ${jmxCollectorsList}"
        jmxCollectorsList.each { registryName,name ->
            log.trace "Initializing ${name}"
            def jmxResource=Holders.getApplicationContext().getResource(name);
            log.trace "Fetched ${name}"
            def ymlString = jmxResource.file.text
            def jc = new JmxCollector(ymlString)
            if (registryName=="default") {
                jc.register()
            } else {
                if (!collectorRegistries[registryName]) {
                    collectorRegistries[registryName]=new CollectorRegistry()
                }
                jc.register(collectorRegistries[registryName])
            }
            log.trace "Initialized ${name}"
            jmxCollectors.add(jc)
        }
    }

}
