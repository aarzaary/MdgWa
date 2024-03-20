package its.madruga.wpp.xposed.plugins.functions;

import static de.robv.android.xposed.XposedHelpers.findClass;
import static its.madruga.wpp.ClassesReference.DndMode.iconOff;
import static its.madruga.wpp.ClassesReference.DndMode.iconOn;
import static its.madruga.wpp.ClassesReference.Others.classObsoleto;
import static its.madruga.wpp.ClassesReference.Others.classProps;
import static its.madruga.wpp.ClassesReference.Others.methodObsoleto;
import static its.madruga.wpp.ClassesReference.Others.methodProps;
import static its.madruga.wpp.ClassesReference.Others.paramProps;
import static its.madruga.wpp.ClassesReference.Others.paramProps2;
import static its.madruga.wpp.xposed.plugins.core.XMain.mApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import its.madruga.wpp.R;
import its.madruga.wpp.xposed.models.XHookBase;

public class XOthers extends XHookBase {

    public static HashMap<Integer, Boolean> props = new HashMap<>();

    public XOthers(ClassLoader loader, XSharedPreferences preferences) {
        super(loader, preferences);
    }

    @Override
    public void doHook() {
        XposedHelpers.findAndHookMethod(classObsoleto, loader, methodObsoleto, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Date date = new Date(10554803081056L);
                param.setResult(date);
            }
        });
        Class<?> propsClass1 = findClass(paramProps, loader);
        Class<?> propsClass2 = findClass(paramProps2, loader);
        var newHome = prefs != null && prefs.getBoolean("novahome", false);
        var novoTema = prefs != null && prefs.getBoolean("novotema", false);
        var menuWIcons = prefs != null && prefs.getBoolean("menuwicon", false);
        var newSettings = prefs != null && prefs.getBoolean("novaconfig", false);
        var filterChats = prefs != null && prefs.getBoolean("novofiltro", false);
        var strokeButtons = prefs != null && prefs.getBoolean("strokebuttons", false);
        var outlinedIcons = prefs != null && prefs.getBoolean("outlinedicons", false);
        var separateGroups = prefs != null && prefs.getBoolean("separategroups", false);

        props.put(3289, newHome);
        props.put(4524, novoTema);
        props.put(4497, menuWIcons);
        props.put(4023, newSettings);
        props.put(5171, filterChats);
        props.put(5834, strokeButtons);
        props.put(5509, outlinedIcons);
        props.put(2358, separateGroups);

        XposedHelpers.findAndHookMethod(classProps, loader, methodProps, propsClass1, propsClass2, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                int i = (int) param.args[2];

                var propValue = props.get(i);

                if (propValue != null) {
                    var stacktrace = Thread.currentThread().getStackTrace();
                    var stackTraceElement = stacktrace[6];
                    if (stackTraceElement != null) {
                        if (stackTraceElement.getClassName().equals("com.whatsapp.HomeActivity$TabsPager")) {
                            if (i == 3289) {
                                param.setResult(false);
                            }
                        } else {
                            param.setResult(propValue);
                        }
                    } else {
                        param.setResult(propValue);
                    }
                }
                super.beforeHookedMethod(param);
            }
        });
        var homeActivity = findClass("com.whatsapp.HomeActivity", loader);
        XposedHelpers.findAndHookMethod(homeActivity, "onCreateOptionsMenu", Menu.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Menu menu = (Menu) param.args[0];
                Activity home = (Activity) param.thisObject;
                if (!newSettings) {
                    var menuItem = menu.add(0, 0, 0, "Restart WhatsApp").setOnMenuItemClickListener(item -> {
                        Intent intent = mApp.getPackageManager().getLaunchIntentForPackage(mApp.getPackageName());
                        if (mApp != null) {

                            home.finishAffinity();
                            mApp.startActivity(intent);
                        }
                        Runtime.getRuntime().exit(0);
                        return true;
                    });

                    if (menuItem.getActionView() != null) {
                        var root = menuItem.getActionView().getRootView();
                        XposedBridge.log("Instance of MenuItem view: " + root.getClass().getName());
                        XposedBridge.log("Instance of MenuItem backg: " + root.getBackground().getClass().getName());
                    }
                }
                var shared = mApp.getSharedPreferences(mApp.getPackageName() + "_mdgwa_preferences", Context.MODE_PRIVATE);
                var dndmode = shared.getBoolean("dndmode", false);
                var cu = mApp.getDrawable(iconOn);
                if (dndmode) {
                    cu = mApp.getDrawable(iconOff);
                }
                var item = menu.add(0, 0, 1, "Dnd Mode " + dndmode);
                item.setIcon(cu);
                item.setShowAsAction(2);
                item.setOnMenuItemClickListener(menuItem -> {
                    if (!dndmode) {
                        new AlertDialog.Builder(home)
                                .setTitle("Aktifke mode DND?")
                                .setMessage("Awas mengko nk gendak'anmu ngechat ra mlebu !!!")
                                .setPositiveButton("Gass", (dialog, which) -> {
                                    shared.edit().putBoolean("dndmode", dndmode ? false : true).commit();
                                    XposedBridge.log(String.valueOf(shared.getBoolean("dndmode", false)));

                                    Intent intent = mApp.getPackageManager().getLaunchIntentForPackage(mApp.getPackageName());
                                    if (mApp != null) {
                                        home.finishAffinity();
                                        mApp.startActivity(intent);
                                    }
                                    Runtime.getRuntime().exit(0);
                                })
                                .setNegativeButton("Rasido", (dialog, which) -> dialog.dismiss())
                                .create().show();
                        return true;
                    }

                    shared.edit().putBoolean("dndmode", dndmode ? false : true).commit();
                    XposedBridge.log(String.valueOf(shared.getBoolean("dndmode", false)));

                    Intent intent = mApp.getPackageManager().getLaunchIntentForPackage(mApp.getPackageName());
                    if (mApp != null) {
                        home.finishAffinity();
                        mApp.startActivity(intent);
                    }
                    Runtime.getRuntime().exit(0);

                    return true;
                });
                super.afterHookedMethod(param);
            }
        });
    }
}
