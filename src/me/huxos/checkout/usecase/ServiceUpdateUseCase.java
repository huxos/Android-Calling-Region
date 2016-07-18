package me.huxos.checkout.usecase;

import com.cabe.lib.cache.http.HttpStringCacheManager;
import com.cabe.lib.cache.http.RequestParams;
import com.cabe.lib.cache.impl.HttpCacheUseCase;
import com.cabe.lib.cache.interactor.HttpCacheRepository;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import me.huxos.checkout.entity.PhoneService;
import rx.Observable;
import rx.functions.Func1;

/**
 * 常用电话服务
 * Created by cabe on 16/7/15.
 */
public class ServiceUpdateUseCase extends HttpCacheUseCase<List<PhoneService>> {
    public ServiceUpdateUseCase() {
        super(null, null);

        RequestParams params = new RequestParams();
        params.host = "http://www.ip138.com/tel.htm";
        setRequestParams(params);

        HttpCacheRepository<String, List<PhoneService>> httpRepository = getHttpRepository();
        if(httpRepository instanceof HttpStringCacheManager) {
            ((HttpStringCacheManager) httpRepository).setStringEncode("gb2312");
        }
        httpRepository.setResponseTransformer(new Observable.Transformer<String, List<PhoneService>>() {
            @Override
            public Observable<List<PhoneService>> call(Observable<String> stringObservable) {
                return stringObservable.map(new Func1<String, List<PhoneService>>() {
                    @Override
                    public List<PhoneService> call(String responseStr) {
                        List<PhoneService> list = new ArrayList<>();

                        Document doc = Jsoup.parse(responseStr);
                        Elements tables = doc.getElementsByTag("table");
                        if(tables != null) {
                            for(int i=0;i<tables.size();i++) {
                                Element table = tables.get(i);
                                Elements trs = table.getElementsByTag("tr");
                                if(trs != null) {
                                    for(int j=0;j<trs.size();j++) {
                                        Element tr = trs.get(j);
                                        Elements tds = tr.getElementsByTag("td");
                                        if(tds != null) {
                                            for(int k=0;k<tds.size()-2;k=k+2) {
                                                Element td1 = tds.get(k);
                                                Element td2 = tds.get(k+1);
                                                String name = td1.text();
                                                String numberStr = td2.text();
                                                if(!numberStr.replaceAll("[^0-9]", "").isEmpty()) {
                                                    if(numberStr.contains("或")) {
                                                        String[] group = numberStr.split("或");
                                                        for(String str : group) {
                                                            list.add(createPhone(name, str));
                                                        }
                                                    } else {
                                                        list.add(createPhone(name, numberStr));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return list;
                    }
                });
            }
        });
    }

    private PhoneService createPhone(String name, String number) {
        return new PhoneService(name, number);
    }
}
