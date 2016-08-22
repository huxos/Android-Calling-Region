package me.huxos.checkout.usecase;

import android.database.Cursor;

import com.cabe.lib.cache.exception.RxException;
import com.cabe.lib.cache.impl.HttpCacheUseCase;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.QueryObservable;
import com.squareup.sqlbrite.SqlBrite;

import me.huxos.checkout.db.DBHelper;
import me.huxos.checkout.entity.PhoneArea;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 数据库归属地查询
 * Created by cabe on 16/7/15.
 */
public class DBLocationUseCase extends HttpCacheUseCase<PhoneArea> {
    private DBHelper dbHelper;
    private String phoneNum;
    public DBLocationUseCase(DBHelper dbHelper, String phoneNum) {
        super(null, null);
        this.dbHelper = dbHelper;
        // 去掉非数字字符
        phoneNum = phoneNum.replaceAll("[^0-9]", "");
        if(phoneNum.length() > 7) {
            // 截取前面7个数字
            phoneNum = phoneNum.substring(0, 7);
        }
        this.phoneNum = phoneNum;
    }

    @Override
    public Observable<PhoneArea> buildHttpObservable() {
        SqlBrite sqlBrite = SqlBrite.create();
        BriteDatabase db = sqlBrite.wrapDatabaseHelper(dbHelper, Schedulers.io());
        QueryObservable query = db.createQuery("phone_location", "select * from phone_location where _id = ?", phoneNum);

        return query.take(1).map(new Func1<SqlBrite.Query, PhoneArea>() {
            @Override
            public PhoneArea call(SqlBrite.Query query) {
                PhoneArea phoneArea = null;
                Cursor c = query.run();
                if(c != null) {
                    if (c.getCount() == 1) {
                        c.moveToNext();
                        Integer id = c.getInt(c.getColumnIndex("_id"));
                        String area = c.getString(c.getColumnIndex("area"));
                        phoneArea = new PhoneArea(id, area);
                        c.close();
                    } else {
                        throw new RxException(0, "no data");
                    }
                }
                return phoneArea;
            }
        });
    }
}
