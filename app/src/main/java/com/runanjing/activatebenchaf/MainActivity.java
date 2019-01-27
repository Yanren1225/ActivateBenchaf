package com.runanjing.activatebenchaf;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

public class MainActivity extends Activity 
{

	private Button adb;
	private Button heiyu;
	private Button root;
	private Button shell;

	private String STR_ADB="adb shell sh /data/data/com.runanjing.activatebenchaf/files/Run.sh";
	private String STR_HEIYU="sh /data/data/com.runanjing.activatebenchaf/files/Run.sh";
	private String STR_SHELL="sh /data/data/com.runanjing.activatebenchaf/files/Run.sh";
	private String kf_packname="com.af.benchaf";

	private CheckBox if_open;

	private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		forceShowOverflowMenu();
		isBenchafInstall(isApplicationAvilible(this, "com.af.benchaf"));
		adb = (Button)findViewById(R.id.adb);
		heiyu = (Button)findViewById(R.id.heiyu);
		root = (Button)findViewById(R.id.root);
		shell = (Button)findViewById(R.id.shell);
		if_open = (CheckBox)findViewById(R.id.if_kf_open);
		SharedPreferences kf_share=getSharedPreferences("kf", 0);
		boolean if_open_bo= kf_share.getBoolean("if_open", true);
		if_open.setChecked(if_open_bo);
		if_open.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					SharedPreferences y=getSharedPreferences("kf", 0);
					SharedPreferences.Editor edit=y.edit();
					edit.putBoolean("if_open", isChecked);
					edit.apply();
				}
			});
		adb.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					cm.setText(STR_ADB);
					Toast.makeText(MainActivity.this, "已经复制到剪切板", Toast.LENGTH_SHORT).show();
				}
			});
		heiyu.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					cm.setText(STR_HEIYU);
					Toast.makeText(MainActivity.this, "已经复制到剪切板", Toast.LENGTH_SHORT).show();
				}
			});
		root.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					String root_ok=Shell.execRootCmd("sh /data/data/com.runanjing.activatebenchaf/files/Run.sh\n");
					if (root_ok.equals("") == false)
					{
						SharedPreferences kf_share=getSharedPreferences("kf", 0);
					   boolean 	if_open_bo= kf_share.getBoolean("if_open", true);
						if (if_open_bo)
						{
							Toast.makeText(MainActivity.this, "激活成功，正在启动快否", Toast.LENGTH_SHORT).show();
							PackageManager packageManager = getPackageManager();
							if (checkPackInfo(kf_packname))
							{
								Intent intent = packageManager.getLaunchIntentForPackage(kf_packname);
								startActivity(intent);
							}
							else
							{
								Toast.makeText(MainActivity.this, "启动失败" + kf_packname, 1).show();
							}
						}
						else
						{
							Toast.makeText(MainActivity.this, "激活成功，请自行启动快否", Toast.LENGTH_SHORT).show();
						}
					}
					else
					{
						Toast.makeText(MainActivity.this, "激活失败，你的设备看起来并没有root权限或者是您拒绝了root权限", Toast.LENGTH_SHORT).show();
					}
				}
			});
		shell.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					cm.setText(STR_SHELL);
					Toast.makeText(MainActivity.this, "已经复制到剪切板", Toast.LENGTH_SHORT).show();
				}
			});
    }

	private void forceShowOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
				.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.about:
				AlertDialog.Builder about_dialog=new AlertDialog.Builder(this)
					.setTitle("关于")
					.setMessage("本应用可以用来激活快否\n开发者:酷安@汝南京\n完全开源，请放心使用")
					.setPositiveButton("确定", null)
					.setNegativeButton("开源地址", new
					DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							Intent github=new Intent("android.intent.action.VIEW");
							github.setData(Uri.parse("https://github.com/nihaocun/ActivateBenchaf"));
							startActivity(github);
						}
					});
				about_dialog.show();
				break;
				
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	private boolean checkPackInfo(String packname)
	{
        PackageInfo packageInfo = null;
        try
		{
            packageInfo = getPackageManager().getPackageInfo(packname, 0);
        }
		catch (PackageManager.NameNotFoundException e)
		{
            e.printStackTrace();
        }
        return packageInfo != null;
    }

	private void isBenchafInstall(boolean isApplicationAvilible)
	{
		if (isApplicationAvilible)
		{
			Toast.makeText(MainActivity.this, "快否正常安装", Toast.LENGTH_SHORT).show();
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("初始化");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.show();
			init();
		}
		else
		{
			AlertDialog.Builder kf_dialog=new AlertDialog.Builder(this)
				.setTitle("警告")
				.setMessage("你还没有安装快否呢")
				.setCancelable(false)
				.setPositiveButton("前往下载", new
				DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						try
						{
							Intent dl_kf=new Intent("android.intent.action.VIEW");
							dl_kf .setData(Uri.parse("market://details?id=com.af.benchaf"));
							dl_kf.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							dl_kf .setPackage("com.coolapk.market");
							startActivity(dl_kf);
						}
						catch (Exception e)
						{
							Intent dl_fk=new Intent("android.intent.action.VIEW");
							dl_fk.setData(Uri.parse("https://www.coolapk.com/apk/com.af.benchaf"));
							startActivity(dl_fk);
						}		
						MainActivity.this.finish();
					}
				}
			)
				.setNegativeButton("退出", new
				DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						MainActivity.this.finish();
					}
				});
			kf_dialog.show();
		}
	}

	private void init(){
		new Thread(new Runnable(){
				@Override
				public void run(){
					try{
						Unzip.unzip(getAssets().open("res.zip"), MainActivity.this.getFilesDir());
					}catch (IOException e){
					}
					initOk();
				}
			}).start();
	}
	private void initOk(){
		runOnUiThread(new Runnable(){

				@Override
				public void run(){
					progressDialog.dismiss();
				}
			});
	}
	
	public static boolean isApplicationAvilible(Context context, String appPackageName)
	{
        PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null)
		{
            for (int i = 0; i < pinfo.size(); i++)
			{
                String pn = pinfo.get(i).packageName;
                if (appPackageName.equals(pn))
				{
                    return true;
                }
            }
        }
        return false;
    }
}
