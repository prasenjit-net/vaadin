package com.example;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.stereotype.Service;

@SpringBootApplication
@EnableRepo(basePackage = "com.example")
public class VaadinApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaadinApplication.class, args);
    }

    @Service
    public static class MyService {
        public String sayHi() {
            return "Hello Spring Initializr!";
        }

    }

    @Theme("valo")
    @SpringUI(path = "")
    public static class VaadinUI extends UI {

        @Autowired
        private MyService myService;

        @Autowired
        private Foo foo;

        @Override
        protected void init(VaadinRequest request) {
            foo.sayHi();
            setContent(new Label(myService.sayHi()));
        }

    }
}
