package tdp.miranking;


import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;

/**
 * Custom action provider used to sort the list view
 * @author Matias Massetti
 * @version Jan-2017
 */
public class MyActionProvider extends ActionProvider {

    private Context mContext;

    public MyActionProvider(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        super.onPrepareSubMenu(subMenu);
        subMenu.clear();
        subMenu.add(0,1,Menu.NONE,"Descendente");
        subMenu.add(0,2,Menu.NONE,"Ascendente");
        subMenu.add(0,3,Menu.NONE,"Predeterminado");
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }

    @Override
    public boolean onPerformDefaultAction() {
        return super.onPerformDefaultAction();
    }

}