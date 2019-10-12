package net.wizardsoflua.reflect;

import java.lang.reflect.Method;
import java.util.List;
import javax.annotation.Nullable;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReflectionUtils_isOverridden_Test extends Assertions {
  private static @Nullable Method getMethodM(Class<?> cls) throws NoSuchMethodException {
    for (Method method : cls.getDeclaredMethods()) {
      if ("m".equals(method.getName())) {
        return method;
      }
    }
    return null;
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Unrelated_Classes() throws Exception {
    // given:
    class A {
      public void m() {}
    }
    class B {
      public void m() {}
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isFalse();
  }

  @Test
  public void test_isOverridden__Public_Method_without_Arguments() throws Exception {
    // given:
    class A {
      public boolean isMOverridden() {
        return m();
      }

      public boolean m() {
        return false;
      }
    }
    class B extends A {
      @Override
      public boolean m() {
        return true;
      }
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);
    boolean expected = new B().isMOverridden();

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void test_isOverridden__Protected_Method_without_Arguments() throws Exception {
    // given:
    class A {
      public boolean isMOverridden() {
        return m();
      }

      protected boolean m() {
        return false;
      }
    }
    class B extends A {
      @Override
      protected boolean m() {
        return true;
      }
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);
    boolean expected = new B().isMOverridden();

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void test_isOverridden__Package_Method_without_Arguments() throws Exception {
    // given:
    class A {
      public boolean isMOverridden() {
        return m();
      }

      boolean m() {
        return false;
      }
    }
    class B extends A {
      @Override
      boolean m() {
        return true;
      }
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);
    boolean expected = new B().isMOverridden();

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void test_isOverridden__Package_Method_without_Arguments_in_different_Package()
      throws Exception {
    // given:
    Method m1 = getMethodM(net.wizardsoflua.reflect.a.A.class);
    Method m2 = getMethodM(net.wizardsoflua.reflect.b.B.class);
    boolean expected = new net.wizardsoflua.reflect.b.B().isMOverridden();

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isEqualTo(expected);
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Private_Method_without_Arguments() throws Exception {
    // given:
    class A {
      public boolean isMOverridden() {
        return m();
      }

      private boolean m() {
        return false;
      }
    }
    class B extends A {
      private boolean m() {
        return true;
      }
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);
    boolean expected = new B().isMOverridden();

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void test_isOverridden__Public_Method_with_same_Argument() throws Exception {
    // given:
    class A {
      public boolean isMOverridden() {
        return m(null);
      }

      public boolean m(String a1) {
        return false;
      }
    }
    class B extends A {
      @Override
      public boolean m(String a1) {
        return true;
      }
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);
    boolean expected = new B().isMOverridden();

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void test_isOverridden__Public_Method_with_different_ArgumentName() throws Exception {
    // given:
    class A {
      public boolean isMOverridden() {
        return m(null);
      }

      public boolean m(String a1) {
        return false;
      }
    }
    class B extends A {
      @Override
      public boolean m(String a2) {
        return true;
      }
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);
    boolean expected = new B().isMOverridden();

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isEqualTo(expected);
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Public_Method_with_different_ArgumentType() throws Exception {
    // given:
    class A {
      public boolean isMOverridden() {
        return m(null);
      }

      public boolean m(String a1) {
        return false;
      }
    }
    class B extends A {
      public boolean m(Integer a1) {
        return true;
      }
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);
    boolean expected = new B().isMOverridden();

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isEqualTo(expected);
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Public_Method_with_more_specific_ArgumentType() throws Exception {
    // given:
    class A {
      public boolean isMOverridden() {
        return m(null);
      }

      public boolean m(CharSequence a1) {
        return false;
      }
    }
    class B extends A {
      public boolean m(String a1) {
        return true;
      }
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);
    boolean expected = new B().isMOverridden();

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void test_isOverridden__Public_Method_with_more_specific_ReturnType() throws Exception {
    // given:
    class A {
      public boolean isMOverridden() {
        return m() != null;
      }

      public CharSequence m() {
        return null;
      }
    }
    class B extends A {
      @Override
      public String m() {
        return "";
      }
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);
    boolean expected = new B().isMOverridden();

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void test_isOverridden__Specified_generic_ClassParam() throws Exception {
    // given:
    class A<T> {
      public boolean isMOverridden() {
        return m(null);
      }

      public boolean m(T t) {
        return false;
      }
    }
    class B extends A<String> {
      @Override
      public boolean m(String t) {
        return true;
      }
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);
    boolean expected = new B().isMOverridden();

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void test_isOverridden__Unspecified_generic_ClassParam() throws Exception {
    // given:
    class A<T> {
      public boolean isMOverridden() {
        return m(null);
      }

      public boolean m(T t) {
        return false;
      }
    }
    class B<S> extends A<S> {
      @Override
      public boolean m(S s) {
        return true;
      }
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);
    boolean expected = new B<>().isMOverridden();

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isEqualTo(expected);
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Unrelated_generic_ClassParam() throws Exception {
    // given:
    class A<T> {
      public boolean isMOverridden() {
        return m(null);
      }

      public boolean m(T t) {
        return false;
      }
    }
    class B<T, S extends CharSequence> extends A<T> {
      public boolean m(S s) {
        return true;
      }
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);
    boolean expected = new B<>().isMOverridden();

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void test_isOverridden__Generic_MethodParam() throws Exception {
    // given:
    class A {
      public boolean isMOverridden() {
        return m(null);
      }

      public <T> boolean m(T t) {
        return false;
      }
    }
    class B extends A {
      @Override
      public <S> boolean m(S s) {
        return true;
      }
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);
    boolean expected = new B().isMOverridden();

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isEqualTo(expected);
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Unrelated_generic_MethodParam() throws Exception {
    // given:
    class A {
      public boolean isMOverridden() {
        return m(null);
      }

      public <T> boolean m(T t) {
        return false;
      }
    }
    class B extends A {
      public <S extends CharSequence> boolean m(S s) {
        return true;
      }
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);
    boolean expected = new B().isMOverridden();

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void test_isOverridden__Specified_generic_MethodParam() throws Exception {
    // given:
    class A<S> {
      public boolean isMOverridden() {
        return m(null);
      }

      public <T extends List<S>> boolean m(T t) {
        return false;
      }
    }
    class B extends A<String> {
      @Override
      public <S extends List<String>> boolean m(S s) {
        return true;
      }
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);
    boolean expected = new B().isMOverridden();

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isEqualTo(expected);
  }
}
