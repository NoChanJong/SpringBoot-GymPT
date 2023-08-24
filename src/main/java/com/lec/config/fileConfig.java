package com.lec.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class fileConfig implements WebMvcConfigurer {

   @Override
   public void addResourceHandlers(ResourceHandlerRegistry registry) {
      registry.addResourceHandler("/upload/**")
         .addResourceLocations("file:///home/ubuntu/uploads/upload/")
         .setCachePeriod(3600)
         .resourceChain(true)
         .addResolver(new PathResourceResolver());
      
      registry.addResourceHandler("/news/**")
         .addResourceLocations("file:///home/ubuntu/uploads/news/")
         .setCachePeriod(3600)
         .resourceChain(true)
         .addResolver(new PathResourceResolver());
      
      registry.addResourceHandler("/informfile/**")
         .addResourceLocations("file:///home/ubuntu/uploads/informfile/")
         .setCachePeriod(3600)
         .resourceChain(true)
         .addResolver(new PathResourceResolver());
      
      registry.addResourceHandler("/image/**")
         .addResourceLocations("file:///home/ubuntu/uploads/image/")
         .setCachePeriod(3600)
         .resourceChain(true)
         .addResolver(new PathResourceResolver());
      
      registry.addResourceHandler("/kimupload/**")
         .addResourceLocations("file:///home/ubuntu/uploads/kimupload/")
         .setCachePeriod(3600)
         .resourceChain(true)
         .addResolver(new PathResourceResolver());
      
      registry.addResourceHandler("/review/**")
         .addResourceLocations("file:///home/ubuntu/uploads/review/")
         .setCachePeriod(3600)
         .resourceChain(true)
         .addResolver(new PathResourceResolver());
      
      registry.addResourceHandler("/kimdownload/**")
         .addResourceLocations("file:///home/ubuntu/uploads/kimdownload/")
         .setCachePeriod(3600)
         .resourceChain(true)
         .addResolver(new PathResourceResolver());
      
      registry.addResourceHandler("/community/**")
         .addResourceLocations("file:///home/ubuntu/uploads/community/")
         .setCachePeriod(3600)
         .resourceChain(true)
         .addResolver(new PathResourceResolver());
   }
   
}
