package chiro.work.roomquery;

import java.lang.reflect.Array;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {
  public static final String cookies = "Cookie: JSESSIONID=27E42ED5EE02D788C983FB482B8F449C; route=3b070995537f3ced53f80b7b8ee3a634";
  public static final String header_01 = "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0";
  public static final String header_02 = "Accept: */*";
  public static final String header_03 = "Accept-Language: zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2";
  public static final String header_04 = "Content-Type: application/x-www-form-urlencoded; charset=UTF-8";
  public static final String header_05 = "X-Requested-With: XMLHttpRequest";
  public static final String header_06 = "Pragma: no-cache";
  public static final String header_07 = "Cache-Control: no-cache";
  // public static final String[] headers = {header_01, header_02, header_03, header_04, header_05, header_06, header_07};

  @Headers({header_01, header_02, header_03, header_04, header_05, header_06, header_07})
  @FormUrlEncoded
  @POST("cdkb/querycdzyrightzhou")
    // pxn=2020-2021&pxq=2&dmmc=&xiaoqu=&jxl=14&cdlb=&zc=0000000000000000100000000000000000&wpksfxs=0&qsjsz=16&pageNum=1&pageSize=19
  Call<List<Data.ClassNode>> getBuildingData(
          @Field("pxn") String year,
          @Field("jxl") int building,
          @Field("zc") String weeks,
          @Field("pageNum") int pageNum,
          @Field("pageSize") int pageSize,
          @Field("pxq") int pxq,
          @Field("dmmc") String codeName,
          @Field("xiaoqu") String xiaoqu,
          @Field("cdlb") String roomType,
          // 是否显示未排课场地
          @Field("wpksfxs") int showDisabled,
          @Field("qsjsz") int qsjsz
  );

  @Headers({header_01, header_02, header_03, header_04, header_05, header_06, header_07})
  @POST("pksd/queryjxlList")
  Call<List<Data.BuildingNode>> getBuildingList(@Header("Cookie") String cookies);

  // @Headers({header_01, header_02, header_03, header_04, header_05, header_06, header_07})
  // @GET("cas")
  // Call
}
