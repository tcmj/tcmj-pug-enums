package com.tcmj.iso.api.model;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

/** @author tcmj */
public class NameTypeValueTest {

  private NameTypeValue getTestInstance() {
    final String[] names = new String[] {"format", "length", "width"};
    final Class[] clazzes = new Class[] {String.class, Integer.class, Integer.class};
    final Object[] values = new Object[] {"m", 40, 20};
    return NameTypeValue.of(names, clazzes, values);
  }

  /** Test of getName method, of class NameTypeValue. */
  @Test
  public void testGetName() {
    NameTypeValue instance = getTestInstance();
    assertThat(instance.getName(), equalTo(new String[] {"format", "length", "width"}));
  }

  /** Test of getType method, of class NameTypeValue. */
  @Test
  public void testGetType() {
    NameTypeValue instance = getTestInstance();
    assertThat(
        instance.getType(), equalTo(new Class[] {String.class, Integer.class, Integer.class}));
  }

  /** Test of getValue method, of class NameTypeValue. */
  @Test
  public void testGetValue() {
    NameTypeValue instance = getTestInstance();
    assertThat(instance.getValue(), equalTo(new Object[] {"m", 40, 20}));
    assertArrayEquals(instance.getValue(), new Object[] {"m", 40, 20});
  }

  /** Test of 'of' method, of class NameTypeValue. */
  @Test
  public void testOf() {
    final String[] names = new String[] {"format", "length", "width"};
    final Class[] clazzes = new Class[] {String.class, Integer.class, Integer.class};
    final Object[] values = new Object[] {"m", 40, 20};
    NameTypeValue nameTypeValue = NameTypeValue.of(names, clazzes, values);
    assertThat("bb = bb", nameTypeValue.getName(), equalTo(names));
    assertThat("bb = bb", nameTypeValue.getType(), equalTo(clazzes));
    assertThat("bb = bb", nameTypeValue.getValue(), equalTo(values));
  }

  /** Test of compareTo method, of class NameTypeValue. */
  @Test
  public void testCompareTo() throws Exception {
    final Class[] clazzes = new Class[] {String.class, String.class};
    final Object[] values = new Object[] {"any", "any"};
    NameTypeValue one = NameTypeValue.of(new String[] {"a", "a"}, clazzes, values);
    NameTypeValue other = NameTypeValue.of(new String[] {"b", "b"}, clazzes, values);
    assertThat("aa < bb", one.compareTo(other), is(-1));
    assertThat("aa = aa", one.compareTo(one), is(0));
    assertThat("bb = bb", other.compareTo(other), is(0));
    assertThat("bb > aa", other.compareTo(one), is(1));
  }

  /** Test of toString method, of class NameTypeValue. */
  @Test
  public void testToString() throws Exception {
    //given
    NameTypeValue instance =
        NameTypeValue.of(
            new String[] {"color", "count"},
            new Class[] {String.class, String.class},
            new Object[] {"blue", "one"});
    //when
    String result = instance.toString();
    //then
    String expResult =
        "{\"name\":[\"color\", \"count\"],\"type\":[\"java.lang.String\", \"java.lang.String\"],\"value\":[\"blue\", \"one\"]}";
    assertThat(result, equalTo(expResult));
  }

  @Test(expected = NullPointerException.class)
  public void shouldNotBeInstantiableWithInvalidValueNullName() {
    NameTypeValue.of(null, new Class[] {String.class}, new Object[] {"blue"});
  }

  @Test(expected = NullPointerException.class)
  public void shouldNotBeInstantiableWithInvalidValueNullType() {
    NameTypeValue.of(new String[] {"color"}, null, new Object[] {"blue"});
  }

  @Test(expected = NullPointerException.class)
  public void shouldNotBeInstantiableWithInvalidValueNullValue() {
    NameTypeValue.of(new String[] {"color"}, new Class[] {String.class}, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotBeInstantiableWithDifferentSizes() {
    NameTypeValue.of(
        new String[] {"aaaa", "bbbb"}, new Class[] {String.class}, new Object[] {"blue"});
  }
}
