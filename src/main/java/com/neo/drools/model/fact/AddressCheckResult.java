package com.neo.drools.model.fact;


public class AddressCheckResult {
    public AddressCheckResult(){

    }
    public AddressCheckResult(int num){
        setCount(num);
    }
    private boolean postCodeResult = false; // true:通过校验；false：未通过校验
    private Integer count = 0;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public boolean isPostCodeResult() {
        return postCodeResult;
    }

    public void setPostCodeResult(boolean postCodeResult) {
        this.postCodeResult = postCodeResult;
    }

    @Override
    public String toString() {
        return "AddressCheckResult{" +
                "postCodeResult=" + postCodeResult +
                ", count=" + count +
                '}';
    }
}
