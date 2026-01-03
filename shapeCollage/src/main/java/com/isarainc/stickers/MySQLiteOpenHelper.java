package com.isarainc.stickers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.isarainc.shapecollage.DaoMaster;
import com.isarainc.shapecollage.ShapeInfoDao;
import com.isarainc.shapecollage.StickerDao;
import com.isarainc.shapecollage.StickerInfoDao;




/**
 * Created by mrnop on 8/30/2016 AD.
 */
public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, StickerDao.class, StickerInfoDao.class,ShapeInfoDao.class);
    }


}