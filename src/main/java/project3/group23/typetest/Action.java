package project3.group23.typetest;

public record Action(Type type, int value) {

    public enum Type
    {
        CHARACTER, ENTER, ESCAPE, ARROW_UP, ARROW_DOWN, BACKSPACE, OTHER
    }

    public Action(Type type)
    { this( type, '\0' ); }

    public Type type()
    { return this.type; }

    public int value()
    { return this.value; }

}
