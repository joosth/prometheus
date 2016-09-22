
class PrometheusBootStrap {
	def grailsApplication
    def metricService
    def init = { servletContext ->
		 metricService.init()
     }

     def destroy = {
     }
}
