package net.wizardsoflua.reflect;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Nullable;

import org.assertj.core.api.Assertions;
import org.junit.Test;

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

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Public_Method_without_Arguments() throws Exception {
    // given:
    class A {
      public void m() {}
    }
    class B extends A {
      @Override
      public void m() {}
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isTrue();
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Protected_Method_without_Arguments() throws Exception {
    // given:
    class A {
      protected void m() {}
    }
    class B extends A {
      @Override
      protected void m() {}
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isTrue();
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Package_Method_without_Arguments() throws Exception {
    // given:
    class A {
      void m() {}
    }
    class B extends A {
      @Override
      void m() {}
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isTrue();
  }

  @Test
  public void test_isOverridden__Package_Method_without_Arguments_in_different_Package()
      throws Exception {
    // given:
    Method m1 = getMethodM(net.wizardsoflua.reflect.a.A.class);
    Method m2 = getMethodM(net.wizardsoflua.reflect.b.B.class);

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isFalse();
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Private_Method_without_Arguments() throws Exception {
    // given:
    class A {
      private void m() {}
    }
    class B extends A {
      private void m() {}
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isFalse();
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Public_Method_with_same_Argument() throws Exception {
    // given:
    class A {
      public void m(String a1) {}
    }
    class B extends A {
      @Override
      public void m(String a1) {}
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isTrue();
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Public_Method_with_different_ArgumentName() throws Exception {
    // given:
    class A {
      public void m(String a1) {}
    }
    class B extends A {
      @Override
      public void m(String a2) {}
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isTrue();
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Public_Method_with_different_ArgumentType() throws Exception {
    // given:
    class A {
      public void m(String a1) {}
    }
    class B extends A {
      public void m(Integer a1) {}
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isFalse();
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Public_Method_with_more_specific_ArgumentType() throws Exception {
    // given:
    class A {
      public void m(CharSequence a1) {}
    }
    class B extends A {
      public void m(String a1) {}
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isFalse();
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Public_Method_with_more_specific_ReturnType() throws Exception {
    // given:
    class A {
      public CharSequence m() {
        return null;
      }
    }
    class B extends A {
      @Override
      public String m() {
        return null;
      }
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isTrue();
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Specified_generic_ClassParam() throws Exception {
    // given:
    class A<T> {
      public void m(T t) {}
    }
    class B extends A<String> {
      @Override
      public void m(String t) {}
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isTrue();
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Unspecified_generic_ClassParam() throws Exception {
    // given:
    class A<T> {
      public void m(T t) {}
    }
    class B<S> extends A<S> {
      @Override
      public void m(S s) {}
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isTrue();
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Unrelated_generic_ClassParam() throws Exception {
    // given:
    class A<T> {
      public void m(T t) {}
    }
    class B<T, S extends CharSequence> extends A<T> {
      public void m(S s) {}
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isFalse();
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Generic_MethodParam() throws Exception {
    // given:
    class A {
      public <T> void m(T t) {}
    }
    class B extends A {
      @Override
      public <S> void m(S s) {}
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isTrue();
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Unrelated_generic_MethodParam() throws Exception {
    // given:
    class A {
      public <T> void m(T t) {}
    }
    class B extends A {
      public <S extends CharSequence> void m(S s) {}
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isFalse();
  }

  @SuppressWarnings("unused")
  @Test
  public void test_isOverridden__Specified_generic_MethodParam() throws Exception {
    // given:
    class A<S> {
      public <T extends List<S>> void m(T t) {}
    }
    class B extends A<String> {
      @Override
      public <S extends List<String>> void m(S s) {}
    }
    Method m1 = getMethodM(A.class);
    Method m2 = getMethodM(B.class);

    // when:
    boolean actual = ReflectionUtils.isOverridden(m1, m2);

    // then:
    assertThat(actual).isTrue();
  }
}
