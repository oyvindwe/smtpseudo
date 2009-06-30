package smtpseudo.smtp.message;

public enum MessageType {
    TEXT(1,"text/plain"),HTML(2,"text/html");
    
    private int type;
    private String contentType;

    MessageType(int type,String contentType){
        this.type=type;
        this.contentType=contentType;
    }
    
    public int value(){
        return type;
    }

    public String getContentType(){
        return contentType;
    }
}
