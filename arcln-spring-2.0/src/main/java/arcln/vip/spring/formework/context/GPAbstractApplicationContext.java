package arcln.vip.spring.formework.context;

public abstract class GPAbstractApplicationContext {

    //提供给子类重写的
    protected void onRefresh() throws Exception {
        // For subclasses: do nothing by default.
    }

    protected abstract void refreshBeanFactory();
}
