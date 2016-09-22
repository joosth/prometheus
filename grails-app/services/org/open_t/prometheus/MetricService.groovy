package org.open_t.prometheus

class MetricService {

    boolean transactional = false

    def init() {
        new io.prometheus.client.hotspot.DefaultExports().initialize();
    }
    
}
