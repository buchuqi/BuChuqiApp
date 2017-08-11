
package jd.com.weight.slideview;

import android.view.View;

public interface BaseAnimationInterface {

    /**
     * When the current item prepare to registerChatConversation leaving the screen.
     * 
     * @param current
     */
    void onPrepareCurrentItemLeaveScreen(View current);

    /**
     * The next item which will be shown in ViewPager/
     * 
     * @param next
     */
    void onPrepareNextItemShowInScreen(View next);

    /**
     * Current item totally disappear from screen.
     * 
     * @param view
     */
    void onCurrentItemDisappear(View view);

    /**
     * Next item totally show in screen.
     * 
     * @param view
     */
    void onNextItemAppear(View view);
}
