package com.tcmj.pug.enums.api.model;

import com.tcmj.pug.enums.model.NameTypeValue;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

/** @author tcmj */
public class NameTypeValueTest {

  private NameTypeValue getTestInstance() {
    return NameTypeValue.of("FORMIDABLE", new Object[]{"m", 500.0D, 20});
  }

  @Test
  public void testGetValue() {
    NameTypeValue instance = getTestInstance();
    assertThat(instance.getValue(), equalTo(new Object[]{"m", 500D, 20}));
    assertArrayEquals(instance.getValue(), new Object[]{"m", 500D, 20});
  }

  @Test
  public void testOf() {
    final Object[] values = new Object[]{"m", 40, 20};
    NameTypeValue nameTypeValue = NameTypeValue.of("ABC", values);
    assertThat("bb = bb", nameTypeValue.getValue(), equalTo(values));
  }

  /** Test of compareTo method, of class NameTypeValue. */
  @Test
  public void testCompareTo() throws Exception {
    NameTypeValue one = NameTypeValue.of("aa");
    NameTypeValue other = NameTypeValue.of("bb");
    assertThat("aa < bb", one.compareTo(other), is(-1));
    assertThat("aa = aa", one.compareTo(one), is(0));
    assertThat("bb = bb", other.compareTo(other), is(0));
    assertThat("bb > aa", other.compareTo(one), is(1));
  }

  /** compareTo should return zero if and only if equals returns true. */
  @Test
  public void testCompareToContract() throws Exception {
    NameTypeValue a = NameTypeValue.of("aa");
    NameTypeValue b = NameTypeValue.of("aa");
    assertThat("a.equals(b) = true", a.equals(b), is(true));
    assertThat("a.compareTo(b) = 0", a.compareTo(b), is(0));
  }

  @Test
  public void testEqualsContract() throws Exception {
    NameTypeValue a = NameTypeValue.of("aa", new Object[]{"xxx", "yyy"});
    NameTypeValue b = NameTypeValue.of("aa", new Object[]{"xxx", "yyy"});
    assertThat("a.equals(b) = true", a.equals(b), is(true));
    assertThat("b.equals(a) = true", b.equals(a), is(true));
    assertThat("a.compareTo(b) = 0", a.compareTo(b), is(0));
    assertThat("b.compareTo(a) = 0", b.compareTo(a), is(0));
  }

  /** Test of toString method, of class NameTypeValue. */
  @Test
  public void testToString() throws Exception {
    //given
    NameTypeValue instance = NameTypeValue.of("COLOR", new Object[]{"blue", "one"});
    //when
    String result = instance.toString();
    //then
    String expResult = "{\"name\":COLOR,\"values\":[\"blue\", \"one\"]}";
    System.out.println(result);
    assertThat(result, equalTo(expResult));
  }

  @Test(expected = NullPointerException.class)
  public void shouldNotBeInstantiableWithInvalidValueNullName() {
    NameTypeValue.of(null, new Object[]{"blue"});
  }

  @Test(expected = NullPointerException.class)
  public void shouldNotBeInstantiableWithInvalidValueNullValue() {
    NameTypeValue.of("CCC", null);
  }

}
