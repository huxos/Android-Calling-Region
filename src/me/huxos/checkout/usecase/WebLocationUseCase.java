package me.huxos.checkout.usecase;

import android.util.Log;
import android.util.Xml;

import com.cabe.lib.cache.http.HttpStringCacheManager;
import com.cabe.lib.cache.http.RequestParams;
import com.cabe.lib.cache.impl.HttpCacheUseCase;
import com.cabe.lib.cache.interactor.HttpCacheRepository;
import com.google.gson.reflect.TypeToken;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import me.huxos.checkout.entity.PhoneArea;
import me.huxos.checkout.entity.Product;
import rx.Observable;
import rx.functions.Func1;

/**
 * 网络归属地查询
 * Created by cabe on 16/7/15.
 */
public class WebLocationUseCase extends HttpCacheUseCase<PhoneArea> {
    public WebLocationUseCase(String phoneNum) {
        super(null, null);

        RequestParams params = new RequestParams();
        params.host = "http://life.tenpay.com/cgi-bin/mobile/MobileQueryAttribution.cgi?chgmobile=" + phoneNum;
        setRequestParams(params);

        HttpCacheRepository<String, PhoneArea> httpRepository = getHttpRepository();
        if(httpRepository instanceof HttpStringCacheManager) {
            ((HttpStringCacheManager) httpRepository).setStringEncode("gb2312");
        }
        httpRepository.setResponseTransformer(new Observable.Transformer<String, PhoneArea>() {
            @Override
            public Observable<PhoneArea> call(Observable<String> stringObservable) {
                return stringObservable.map(new Func1<String, PhoneArea>() {
                    @Override
                    public PhoneArea call(String s) {
                        PhoneArea phoneArea = null;
                        InputStream is = null;
                        try {
                            is = new ByteArrayInputStream(s.getBytes("gb2312"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        try {
                            //解析XML
                            List<Product> products = parseXML(is);
                            if (products.size() == 1) {
                                Product product = products.get(0);
                                String phoneNum = product.getPhonenum();
                                String location = product.getLocation();

                                phoneArea = new PhoneArea(Integer.parseInt(phoneNum.substring(0, 7)), location.replaceAll(" ", ""));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return phoneArea;
                    }
                });
            }
        });
    }

    private List<Product> parseXML(InputStream inputStream) throws XmlPullParserException, IOException {
        List<Product> products = new ArrayList<>();
        Product product = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(inputStream, "gb2312");
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_TAG:
                    if ("root".equals(parser.getName())) {
                        product = new Product();
                    } else if(product != null) {
                        if ("chgmobile".equals(parser.getName())) {
                            product.setPhonenum(parser.nextText());
                        } else if ("city".equals(parser.getName())) {
                            product.setCity(parser.nextText());
                            Log.i("RemoteHelper", "city:" + product.getLocation());
                        } else if("province".equals(parser.getName())) {
                            product.setProvince(parser.nextText());
                            Log.i("RemoteHelper", "province:" + product.getLocation());
                        } else if("supplier".equals(parser.getName())) {
                            product.setSupplier(parser.nextText());
                            Log.i("RemoteHelper", "supplier:" + product.getLocation());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("root".equals(parser.getName())) {
                        products.add(product);
                        product = null;
                    }
                    break;
            }
            event = parser.next();
        }
        return products;
    }
}
