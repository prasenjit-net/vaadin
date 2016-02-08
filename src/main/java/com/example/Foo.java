package com.example;

import org.springframework.stereotype.Repository;

/**
 * Created by PRASEN on 2/6/2016.
 */
@Repository
public interface Foo {
    @Greeting("Say hi to the world!!!")
    public void sayHi();
}
