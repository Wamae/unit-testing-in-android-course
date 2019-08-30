package com.techyourchance.unittestingfundamentals.exercise1;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class NegativeNumberValidatorTest {
    NegativeNumberValidator SUT;

    @Before
    public void setup(){
        SUT = new NegativeNumberValidator();
    }

    @Test
    public void isPositiveNumber(){
        Assert.assertThat(SUT.isNegative(1), is(false));
    }

    @Test
    public void isZero(){
        Assert.assertThat(SUT.isNegative(0),is(false));
    }

    @Test
    public void isNegativeNumber(){
        Assert.assertThat(SUT.isNegative(-1),is(true));
    }


}