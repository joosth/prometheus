package org.open_t.prometheus
import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import java.io.Writer;
class MetricsController {

    def index = {

        response.setHeader("Content-Type", TextFormat.CONTENT_TYPE_004)

        Writer writer = response.getWriter();
        TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples());
        writer.flush();
        writer.close();
    }
}

