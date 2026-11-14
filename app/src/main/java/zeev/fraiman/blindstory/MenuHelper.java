package zeev.fraiman.blindstory;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;

public class MenuHelper {

    public static void setupMenuIcon(Activity activity, View menuAnchorView) {
        if (menuAnchorView == null) return;

        menuAnchorView.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(activity, v);
            popup.getMenuInflater().inflate(R.menu.top_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> handleOptionsItemSelected(activity, item));
            popup.show();
        });
    }

    public static boolean handleOptionsItemSelected(Activity activity, MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_guide) {
            if (!(activity instanceof GuideActivity)) {
                Intent intent = new Intent(activity, GuideActivity.class);
                activity.startActivity(intent);
            }
            return true;
        } else if (id == R.id.menu_back) {
            activity.finish();
            return true;
        } else if (id == R.id.menu_exit) {
            activity.finishAffinity();
            return true;
        }
        return false;
    }
}