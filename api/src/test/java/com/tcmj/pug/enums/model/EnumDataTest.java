package com.tcmj.pug.enums.model;

import com.tcmj.pug.enums.api.tools.EnumDataHelper;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

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
    cut.getData().stream().map(t -> t.getConstantName()).forEach(a -> assertThat(a, CoreMatchers.anyOf(equalTo("BLUE"), equalTo("RED"), equalTo("GREEN"))));
  }
  
}
