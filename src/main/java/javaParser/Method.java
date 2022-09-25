package javaParser;

public class Method {
    private String signature;
    private String comment;
    private String body;
    private String method;

    public Method(String signature, String comment, String body, String method){
        this.body = body;
        this.method = method;
        this.signature = signature;
        this.comment = comment;
    }
    public Method(){}

    public void setBody(String body) {
        this.body = body;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
