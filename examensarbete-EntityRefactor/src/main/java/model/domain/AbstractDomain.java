package model.domain;

public abstract class AbstractDomain {
    private int index;

    private String realValue;

    public AbstractDomain(int index, String realValue){
        setIndex(index);
        setRealValue(realValue);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index + 1;
    }

    public String getRealValue() {
        return realValue;
    }

    public void setRealValue(String realValue) {
        this.realValue = realValue;
    }

    @Override
    public String toString(){
        return "[" + getClass().getSimpleName().toLowerCase() + ":" + index + "]";
    }
}
