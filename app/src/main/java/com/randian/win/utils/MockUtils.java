package com.randian.win.utils;

import com.randian.win.model.Coach;
import com.randian.win.model.Sport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lily on 16/8/27.
 */
public class MockUtils {
    public static List<Sport> getSportList(String type){
        List<Sport> sports = new ArrayList<>();

        for(int i = 0;i<10;i++){
            Sport sport = new Sport();
            sport.setId(i);
            sport.setCoach_price((i + 1) * 100);
            sport.setCreated_at("2016-08-27");
            sport.setDescription("专业教练，有丰富的经验");
            sport.setHead_image_url("http://www.ijiaolian.com/avatar/000/00/62/03_avatar_big.jpg");
            sport.setDuration(90);
            sport.setMax_user_num(20);
            sport.setMin_user_num(10);
            if("gym".equals(type)) {
                sport.setName("李博天");
            }else{
                sport.setName("文琪");
            }
            sport.setOriginal_price(1000);
            sport.setSuggest("需要减肥，空腹");
            List<String> pics = new ArrayList<>();
            pics.add("http://img2.imgtn.bdimg.com/it/u=4160810923,4108167293&fm=21&gp=0.jpg");
            pics.add("http://img3.redocn.com/20091013/20091013_74508eba6a9bf65760c8UrZFi3f5YHdF.jpg");
            pics.add("http://k.sinaimg.cn/www/ty/additional/2015-07-07/U8451P6T12D7650066F44DT20150707155611.jpg/w570b7b.jpg");
            sport.setDetail_image_urls(pics);
            sports.add(sport);
        }
        return sports;
    }

    public static List<Coach> getCoachList(String type){
        List<Coach> coaches = new ArrayList<>();

        for(int i = 0;i<10;i++){
            Coach coach = new Coach();
            if("gym".equals(type)) {
                coach.setSex("男");
                coach.setName("李博天");
                coach.setProfile_image_url("http://www.ijiaolian.com/avatar/000/00/62/03_avatar_big.jpg");
                coach.setAvailable_areas("知春路");
                coach.setCategories("搏击 健身");
            }else{
                coach.setSex("女");
                coach.setName("文琪");
                coach.setAvailable_areas("沿海赛洛城");
                coach.setCategories("瘦身操");
                coach.setProfile_image_url("http://s.114chn.com/HeadPic/big_201307070103.jpeg");
            }
            coach.setCity("北京");
            coach.setCoach_price(i * 100);
            coach.setComment_num(i);
            coach.setDescription("健身俱乐部健身教练，分为团体操课教练和私人教练，私人健身教练是指在健身俱乐部中一对一进行指导训练的专业健身教练，其作具有互动性、针对性等特点，并且是按课时收费的。");
            coach.setDistance(i*100);
            coach.setLevel(1);
            coach.setOrder_num(i*100);
            coach.setScore(4.5f);
            coaches.add(coach);
        }
        return coaches;
    }
}
