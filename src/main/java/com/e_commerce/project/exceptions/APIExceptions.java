package com.e_commerce.project.exceptions;

public class APIExceptions extends RuntimeException{

//     for serialization over the network
    private static final long serialVersionUID=1L;


    public APIExceptions() {
    }

    public APIExceptions(String message) {
        super(message);
    }
}
