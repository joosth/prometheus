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
import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import java.io.Writer;
class MetricsController {
    def metricService
    
    def metrics() {

        request.headerNames.each { headerName ->
          log.trace "Received header name: ${headerName} value: ${request.getHeader(headerName)}"
        }

        if (grailsApplication.config.prometheus?.auth?.type=="basic") {
            log.trace "Performing basic authentication ..."
            String headerDigest=request.getHeader("authorization")
            String configDigest="Basic "+"${grailsApplication.config.prometheus?.auth?.username}:${grailsApplication.config.prometheus?.auth?.password}".bytes.encodeBase64().toString()
            if (configDigest!=headerDigest) {
                log.trace "Basic authentication failed, returning. configDigest=${configDigest} headerDigest=${headerDigest}"
                render (text:"")
                return null
            } else {
                log.trace "Basic authentication passed."
            }
        }

        response.setHeader("Content-Type", TextFormat.CONTENT_TYPE_004)

        Writer writer = response.getWriter();
        if (!params.id || params.id=="default") {
            TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples());
        } else {
            TextFormat.write004(writer, metricService.collectorRegistries[params.id].metricFamilySamples());
        }
        writer.flush();
        writer.close();
        return null
    }
}
