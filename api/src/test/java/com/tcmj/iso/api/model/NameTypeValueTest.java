package com.tcmj.iso.api.model;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

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
