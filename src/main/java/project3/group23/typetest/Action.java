package project3.group23.typetest;

public record Action(Type type, char value) {

    public enum Type
    {
        CHARACTER, BACKSPACE, ENTER, ESCAPE, ARROW_UP, ARROW_DOWN
    }

    public Action(Type type)
    { this( type, '\0' ); }

    public Type type()
    { return this.type; }

    public char value()
    { return this.value; }

}
