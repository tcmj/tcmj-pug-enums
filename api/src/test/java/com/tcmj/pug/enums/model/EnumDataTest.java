package com.tcmj.pug.enums.model;

import com.tcmj.pug.enums.api.tools.EnumDataHelper;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Test of class EnumData.
 */
public class EnumDataTest {

  EnumData cut; //component under test

  @Before
  public void setUp() {
    cut = new EnumData();
  }

  @Test
  public void isEnumWithSubfieldsShouldReturnFalseInitially() {
    assertThat(cut.isEnumWithSubfields(), is(false));
  }

  @Test
  public void isEnumWithSubfieldsShouldReturnFalseHavingOnlyAConstantNameRecord() {
    EnumDataHelper.addConstantWithoutSubfield(cut, "RED");
    assertThat(cut.isEnumWithSubfields(), is(false));
  }

  @Test
  public void isEnumWithSubfieldsShouldReturnTrueHavingAConstantNameWithSubfieldRecord() {
    cut.setFieldNames("color");        //needed to not throw NullPointer adding subfield value records 
    cut.setFieldClasses(String.class); //needed to not throw NullPointer adding subfield value records 
    EnumDataHelper.addConstantValue(cut, "RED", "#FF0000");
    assertThat(cut.isEnumWithSubfields(), is(true));
  }

  @Test
  public void isEnumWithSubfieldsShouldReturnTrueHavingAConstantNameWithSubfieldRecordButNoFieldNames() {
    cut.getData().add(NameTypeValue.of("RED", new Object[]{"#FF0000"}));
    assertThat(cut.isEnumWithSubfields(), is(true));
  }

  @Test
  public void isEnumWithSubfieldsShouldReturnTrueIfEmptyButAlreadyFieldNamesSet() {
    cut.setFieldNames("color");
    assertThat(cut.isEnumWithSubfields(), is(true));
  }

  @Test
  public void getDataForEachInCaseOfNoSubfields() {
    //given
    EnumDataHelper.addConstantWithoutSubfield(cut, "RED");
    EnumDataHelper.addConstantWithoutSubfield(cut, "GREEN");
    EnumDataHelper.addConstantWithoutSubfield(cut, "BLUE");
    //when
    cut.getData().stream().map(NameTypeValue::getConstantName).forEach(a -> assertThat(a, CoreMatchers.anyOf(equalTo("BLUE"), equalTo("RED"), equalTo("GREEN"))));
  }

  @Test
  public void testSetClassNameWithPackage() {
    //when
    cut.setClassName("pa.ck.age.AnotherClass");
    //then
    assertThat("getClassName", cut.getClassName(), equalTo("pa.ck.age.AnotherClass"));
    assertThat("getClassNameSimple", cut.getClassNameSimple(), equalTo("AnotherClass"));
    assertThat("getPackageName", cut.getPackageName(), equalTo("pa.ck.age"));
  }

  @Test
  public void testSetClassNameWithoutPackage() {
    //when
    cut.setClassName("ClassOnly");
    //then
    assertThat("getClassName", cut.getClassName(), equalTo("ClassOnly"));
    assertThat("getClassNameSimple", cut.getClassNameSimple(), equalTo("ClassOnly"));
    assertThat("getPackageName", cut.getPackageName(), nullValue());
  }

  @Test
  public void getFieldClass() {
    //given
    cut.setFieldClasses(String.class, Integer.class, Boolean.class);
    assertThat("1", cut.getFieldClass(0), equalTo(String.class));
    assertThat("2", cut.getFieldClass(1), equalTo(Integer.class));
    assertThat("3", cut.getFieldClass(2), equalTo(Boolean.class));
  }

  @Test(expected = IllegalStateException.class)
  public void getFieldClassShouldReturnExceptionWhenNothingSet1() {
    cut.getFieldClass(-1);
  }

  @Test(expected = IllegalStateException.class)
  public void getFieldClassShouldReturnExceptionWhenNothingSet2() {
    cut.getFieldClass(0);
  }

  @Test(expected = IllegalStateException.class)
  public void getFieldClassShouldReturnExceptionWhenNothingSet3() {
    cut.getFieldClass(1);
  }

  @Test(expected = IllegalStateException.class)
  public void getFieldClassExceedsUpperBound() {
    //when one is set
    cut.setFieldClasses(String.class);
    cut.getFieldClass(1);
  }

}
