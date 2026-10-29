package project3.group23.typetest;

public class BaseTest extends student.TestCase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    println("Hello, " + getName());
  }

  public void print(boolean b) {
    super.out().print(b);
  }

  public void print(char c) {
    super.out().print(c);
  }

  public void print(int i) {
    super.out().print(i);
  }

  public void print(long l) {
    super.out().print(l);
  }

  public void print(float f) {
    super.out().print(f);
  }

  public void print(double d) {
    super.out().print(d);
  }

  public void print(char[] s) {
    super.out().print(s);
  }

  public void print(String s) {
    super.out().print(s);
  }

  public void print(Object o) {
    super.out().print(o);
  }

  public void println(boolean b) {
    super.out().println(b);
  }

  public void println(char c) {
    super.out().println(c);
  }

  public void println(int i) {
    super.out().println(i);
  }

  public void println(long l) {
    super.out().println(l);
  }

  public void println(float f) {
    super.out().println(f);
  }

  public void println(double d) {
    super.out().println(d);
  }

  public void println(char[] s) {
    super.out().println(s);
  }

  public void println(String s) {
    super.out().println(s);
  }

  public void println(Object o) {
    super.out().println(o);
  }

  public String[] history() {
    return out().getHistory().split("\n");
  }
}
