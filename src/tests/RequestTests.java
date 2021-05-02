package tests;

import core.Request;

public class RequestTests {
    public static void main(String[] args) {
        Request request1 = new Request("https://docs.scala-lang.org/", "GET", null);
        System.out.println(request1.output);

        Request request2 = new Request("https://reqres.in/api/users", "POST", "{\"name\": \"Li Xi\", \"job\": \"Java POST\"}");
        System.out.println(request2.output);

        Request request3 = new Request(
                "https://api.github.com/",
                "GET",
                null,
                new String[][] {
                        {"Accept", "*/*"},
                        {"User-Agent", "*"}
                });
        System.out.println(request3.output);
    }
}