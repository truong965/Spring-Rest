package vn.hoidanit.jobhunter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// configure the converters to use the ISO format for dates by default
public class DateTimeFormatConfiguration implements WebMvcConfigurer {
      @Override
      public void addFormatters(FormatterRegistry registry) {
            // TODO Auto-generated method stub
            DateTimeFormatterRegistrar dateTimeFormatterRegistrar = new DateTimeFormatterRegistrar();
            dateTimeFormatterRegistrar.setUseIsoFormat(true);
            dateTimeFormatterRegistrar.registerFormatters(registry);
      }
}
