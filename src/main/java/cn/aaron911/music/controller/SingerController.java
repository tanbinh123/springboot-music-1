package cn.aaron911.music.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import cn.aaron911.minio.util.MinIoUtils;
import cn.aaron911.music.constant.Const;
import cn.aaron911.music.domain.Singer;
import cn.aaron911.music.service.SingerService;

@RestController
@Controller
public class SingerController {
	
	@Autowired
    private MinIoUtils minIoUtils;

    @Autowired
    private SingerService singerService;

//    添加歌手
    @ResponseBody
    @RequestMapping(value = "/singer/add", method = RequestMethod.POST)
    public Object addSinger(
		HttpServletRequest req,
		String name,
		String sex,
		String pic,
		String birth,
		String location,
		String introduction	
	){
        JSONObject jsonObject = new JSONObject();
        name = name.trim();
        sex = sex.trim();
        pic = pic.trim();
        birth = birth.trim();
        location = location.trim();
        introduction = introduction.trim();

        Singer singer = new Singer();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date myBirth = new Date();
        try {
            myBirth = dateFormat.parse(birth);
        }catch (Exception e){
            e.printStackTrace();
        }
        singer.setName(name);
        singer.setSex(new Byte(sex));
        singer.setPic(pic);
        singer.setBirth(myBirth);
        singer.setLocation(location);
        singer.setIntroduction(introduction);

        boolean res = singerService.addSinger(singer);
        if (res){
            jsonObject.put("code", 1);
            jsonObject.put("msg", "添加成功");
            return jsonObject;
        }else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "添加失败");
            return jsonObject;
        }
    }

//    返回所有歌手
    @RequestMapping(value = "/singer", method = RequestMethod.GET)
    public Object allSinger(){
        return singerService.allSinger();
    }

//    根据歌手名查找歌手
    @RequestMapping(value = "/singer/name/detail", method = RequestMethod.GET)
    public Object singerOfName(HttpServletRequest req){
        String name = req.getParameter("name").trim();
        return singerService.singerOfName(name);
    }

//    根据歌手性别查找歌手
    @RequestMapping(value = "/singer/sex/detail", method = RequestMethod.GET)
    public Object singerOfSex(HttpServletRequest req){
        String sex = req.getParameter("sex").trim();
        return singerService.singerOfSex(Integer.parseInt(sex));
    }

//    删除歌手
    @RequestMapping(value = "/singer/delete", method = RequestMethod.GET)
    public Object deleteSinger(HttpServletRequest req){
        String id = req.getParameter("id");
        return singerService.deleteSinger(Integer.parseInt(id));
    }

//    更新歌手信息
    @ResponseBody
    @RequestMapping(value = "/singer/update", method = RequestMethod.POST)
    public Object updateSingerMsg(HttpServletRequest req){
        JSONObject jsonObject = new JSONObject();
        String id = req.getParameter("id").trim();
        String name = req.getParameter("name").trim();
        String sex = req.getParameter("sex").trim();
        String pic = req.getParameter("pic").trim();
        String birth = req.getParameter("birth").trim();
        String location = req.getParameter("location").trim();
        String introduction = req.getParameter("introduction").trim();

        Singer singer = new Singer();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date myBirth = new Date();
        try {
            myBirth = dateFormat.parse(birth);
        }catch (Exception e){
            e.printStackTrace();
        }
        singer.setId(Integer.parseInt(id));
        singer.setName(name);
        singer.setSex(new Byte(sex));
        singer.setPic(pic);
        singer.setBirth(myBirth);
        singer.setLocation(location);
        singer.setIntroduction(introduction);

        boolean res = singerService.updateSingerMsg(singer);
        if (res){
            jsonObject.put("code", 1);
            jsonObject.put("msg", "修改成功");
            return jsonObject;
        }else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "修改失败");
            return jsonObject;
        }
    }

//    更新歌手头像
    @ResponseBody
    @RequestMapping(value = "/singer/avatar/update", method = RequestMethod.POST)
    public Object updateSingerPic(@RequestParam("file") MultipartFile avatorFile, 
    		@RequestParam("id")int id
    ){
        JSONObject jsonObject = new JSONObject();

        if (avatorFile.isEmpty()) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "文件上传失败！");
            return jsonObject;
        }
        String fileName = System.currentTimeMillis() + "_" + avatorFile.getOriginalFilename();
//        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "img" + System.getProperty("file.separator") + "singerPic" ;
//        File file1 = new File(filePath);
//        if (!file1.exists()){
//            file1.mkdir();
//        }
//
//        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
        String storeAvatorPath = "img/singerPic/"+fileName;
        try {
        	minIoUtils.putObject(Const.MINIO_BUCKET_MUSIC, storeAvatorPath, avatorFile.getInputStream());
            //avatorFile.transferTo(dest);
            Singer singer = new Singer();
            singer.setId(id);
            singer.setPic(storeAvatorPath);
            boolean res = singerService.updateSingerPic(singer);
            if (res){
                jsonObject.put("code", 1);
                jsonObject.put("pic", storeAvatorPath);
                jsonObject.put("msg", "上传成功");
                return jsonObject;
            }else {
                jsonObject.put("code", 0);
                jsonObject.put("msg", "上传失败");
                return jsonObject;
            }
        }catch (Exception e){
            jsonObject.put("code", 0);
            jsonObject.put("msg", "上传失败" + e.getMessage());
            return jsonObject;
        }
    }
}

