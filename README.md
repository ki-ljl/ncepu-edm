![](https://img.shields.io/badge/NCEPU-Android-red)
# 1 APP介绍
## 1.1 说明
&emsp;&emsp;**NCEPU-EDM**（NCEPU和EDM分别是华北电力大学和教育数据挖掘的缩写）软件是专门为华北电力大学本科生所开发的一款简单软件，具有查询和数据挖掘两大功能模块。其中查询模块为学生提供成绩、课表、考试、GPA、培养方案、成绩总表以及综合测评等教务查询。同时该模块对学生的成绩数据进行可视化分析，包括成绩占比、成绩比较、GPA走势、单科分析、专业排名、挂科分析、单科排名以及个人分析查询。数据挖掘模块对近五年的学生成绩数据进行了关联分析，生成了几十条有用的关联规则，利用关联规则学生可以根据以前考试科目的成绩来大致预测将来考试科目的成绩。同时该模块根据关联规则，选取了关联性较强的一些科目，利用部分科目的成绩来预测特定科目的成绩，并用SVM、KNN等六个机器学习算法来训练模型，进而预测相关成绩，让学生可以根据预测情况进行相应学习状态或者复习状态的调整，最终达到成绩预警的作用。

## 1.2 软件登录
&emsp;&emsp;第一次打开APP时，会提示用户登录。从登录界面中用户可以看到需要输入学号，教务系统密码以及内网密码，待输入完成之后点击login按钮，如果三项信息匹配，则会进入到查询界面，否则会具体提示到底是教务系统密码错误还是内网密码错误，详情如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/4bd5a6d7ff16469591602fb5c0f33dd0.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

## 1.3 查询主界面
&emsp;&emsp;用户输入完正确的学号、教务系统密码以及内网密码之后，点击界面下方的登录按钮，用户即可进入该软件。用户进入该软件之后，便会看到软件的查询主界面，并可对查询界面的所有功能进行设置与操作，详情如下图所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/a758004d0adb42139427215d40fa1263.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

## 1.4 挖掘主界面
![在这里插入图片描述](https://img-blog.csdnimg.cn/289949ae980442549dfee520f3d402f8.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

&emsp;&emsp;在这个界面，用户可以查看一些挖掘好的关联规则，同时利用六个机器学习算法对自己的成绩做出预测，以达到成绩预警的效果。

## 1.5 个人中心
&emsp;&emsp;用户继续向左滑动，可以进入个人中心界面，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/5f929e0d85fa4d3d9d914ab435181838.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70)

&emsp;&emsp;这个界面用于展示一些个人信息以及退出当前账号等功能。

# 2 查询功能
## 2.1 个人教务
### 2.1.1 成绩查询
&emsp;&emsp;用户点击查询主界面的成绩查询按钮，进入到成绩查询界面，然后选择学年、学期以及课程性质三项信息，点击查询按钮，结果如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/e0aa0219b5804182a12be0c8a18c5f37.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70)

### 2.1.2 课表查询
&emsp;&emsp;用户点击查询主界面的课表查询按钮，进入到课表查询界面，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/431b7d97cba6433aaf592f2b0552f9d3.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)
&emsp;&emsp;用户可以在该界面看到自己的本学期的课表信息。用户继续点击右上角，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/3fc64fc309324d3a944d5befe025c742.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)
&emsp;&emsp;右上角弹出了修改当前周数、时间设置以及背景设置三个选项。点击进入修改当前周数界面，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/26784b7eaae747788341e249820835a2.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

&emsp;&emsp;用户在这里可以修改当前周数，修改后周数会随着学期进行而自动增加。接着点击进入时间设置界面：
![在这里插入图片描述](https://img-blog.csdnimg.cn/ce18f83e5cb14058b74a9e61f18737bf.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)
&emsp;&emsp;用户在这里可以对每一节课的时间进行设置，设置完成后APP会在上课前提示用户接下来应该上什么课程。最后点击进入背景设置界面：
![在这里插入图片描述](https://img-blog.csdnimg.cn/16848bd3d3a740e59a120d5405b97f23.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)
&emsp;&emsp;用户在这里可以进行更换课表背景以及设置透明度，设置完成之后点击右上角应用即可。

### 2.1.3 考试安排
&emsp;&emsp;用户点击查询主界面的考试安排按钮，进入到考试安排界面，然后选定学年以及学期信息，点击查询按钮，就可以看到本学期的所有考试信息，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/f47c18ed356a453aa9c1e5d165962fb7.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

### 2.1.4 GPA查询
&emsp;&emsp;用户点击查询主界面的GPA按钮，进入到GPA查询界面，选定学年、学期以及查询性质信息，然后点击查询按钮，就能看到相应的GPA信息，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/9bc5b96fda8c4cb89fbd2fccdf5ec73e.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

### 2.1.5 培养方案查询
&emsp;&emsp;用户点击查询主界面的培养方案按钮，进入到培养方案查询界面，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/a000b84da4754362adb5b372fdb28567.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAQ3lyaWxfS0k=,size_20,color_FFFFFF,t_70,g_se,x_16#pic_center)
&emsp;&emsp;用户在这里可以看到自己大学期间必修课、实践课、专选课的课程要求。

### 2.1.6 成绩总表查询
&emsp;&emsp;用户点击查询主界面的成绩总表按钮，进入到成绩总表查询界面，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/5e2de60035874e04ae6a5f814b5afbea.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

&emsp;&emsp;该表展示了用户截止到目前为止一共获得的学分以及每一门课程的成绩信息。

### 2.1.7 综合测评查询
&emsp;&emsp;用户点击查询主界面的综合测评查询按钮，如果是第一次登录，会提示用户输入数字华电密码，输入后点击登录进入到综合测评查询界面，然后点击查询，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/a71dbcc915a34c38be8b825c701bc460.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

## 2.2 数据可视化
### 2.2.1 成绩占比
&emsp;&emsp;用户点击查询主界面的成绩占比按钮，进入到成绩占比查询界面，选好学年、学期以及课程性质，最后点击查询，就会出现用户本学期该类课程各分数阶段的比例，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/cb4262a5ae5c4afbac45d28fb19e57d6.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

### 2.2.2 成绩比较
&emsp;&emsp;用户点击查询主界面的成绩比较按钮，进入到成绩比较查询页面，选好学年、学期以及查询性质，最后点击查询，就能看到每一门课与该段时间平均学分绩的比较情况，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/6848f69352d747779f4a4f907d38c1f3.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

### 2.2.3 GPA走势
&emsp;&emsp;用户点击查询主界面中的GPA走势按钮，进入到GPA走势查询界面，选定查询性质，点击查询，会出现用户从大一上至今每一学期GPA的变化趋势，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/333f0e68a0e04cd28ee27a405ba7a945.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

### 2.2.4 单科分析
&emsp;&emsp;用户点击查询主界面的单科分析按钮，进入到单科分析查询界面，在搜索框输入一门课程的部分名字，下方会出现可能的课程名字选项，点击选中，最后点击查询，会出现该门课程的前五名分数，同时还有该门课程每一分数阶段的人数占比以及学科平均分，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/2eedcab628df48e8812f4d2ed6b31c5d.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

### 2.2.5 专业排名
&emsp;&emsp;用户点击查询主界面的专业排名按钮，进入到专业排名界面，选定学年、学期以及查询性质，然后点击查询，就可以看到该学年学期该类课程性质（比如必修+实践）下自己的GPA以及专业排名，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/0ee758ec9b444e548a8736d046397496.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

### 2.2.6 挂科分析
&emsp;&emsp;用户点击查询主界面的挂科分析按钮，进入到挂科分析界面，选定学年、学期信息，然后点击查询按钮，就可以看到该学年学期下本专业每门课的挂科情况，包括挂科总人数以及挂科学生的成绩分布，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/f339d030180e4ee0925109cf23308f31.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

### 2.2.7 单科排名
&emsp;&emsp;用户点击查询主界面的单科排名按钮，进入到单科排名查询界面，在搜索框中输入科目名称，然后点击输入框右边的搜索图标，就会出现用户该门课程的分数以及在本专业该门课程的排名，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/9cf2f4d70b80457186080be04f698526.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)


### 2.2.8 个人分析
&emsp;&emsp;用户点击查询主界面的个人分析按钮，进入到个人分析界面，然后点击生成雷达图，就能看到自己从大一至今在数理、专业、体育、政治、实践以及外语等六个方面的大致得分，进而看出自己哪方面具有优势以及哪方面具有劣势，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/64b442b499684f1095cc820128677754.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)


# 3 数据挖掘功能
## 3.1 关联分析
&emsp;&emsp;用户点击挖掘主界面的专业课（或数理课和政治课）按钮，进入到专业课关联分析界面，然后点击生成关联规则，就能看到对学校近五届学生专业课成绩关联分析所挖掘出的一些关联规则，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/463ee0f9125443538c02d966fd666e30.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

## 3.2 成绩预测
&emsp;&emsp;这部分是利用AdaBoost、DecisionTree等六种机器学习算法对近五届的学生成绩进行训练建模，然后对用户的各科成绩进行预测，进而达到成绩预警的目的。六个算法的预测界面以及预测科目是一样的，只是预测算法以及模型准确度不一样，所以下面只是展示第一个机器学习算法界面。

&emsp;&emsp;用户点击挖掘主界面的AdaBoost按钮，进入到AdaBoost算法预测界面，点击预测，APP会提示正在加载，加载完成后用户可以看到很多条关于自己成绩的预测，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/21b6bc47d41e4315a13bd9251e4708f3.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)

&emsp;&emsp;我们点击其中一条，例如第二条：
![在这里插入图片描述](https://img-blog.csdnimg.cn/09478a3207874cfba3949895d4eb2506.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70#pic_center)
&emsp;&emsp;可以看到，屏幕正中间出现了关于第二条规则的详细预测过程以及每一个模型的准确率，最终根据这个预测过程，我们就得到了该门课程的预测分数，如果预测的是尚未考试的科目，预测结果就会对学生的学习以及复习过程有指导意义（A代表90分以上，B代表80-90分，以此类推）。

# 4 个人中心
&emsp;&emsp;个人中心如下图所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/8907464e98e04f878f1a8b34d10e1f1e.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0N5cmlsX0tJ,size_16,color_FFFFFF,t_70)

## 4.1 头像修改
&emsp;&emsp;用户点击个人中心上方个人头像图标，系统会让用户自己在图库中选择相应图片，选择后点击确认即可。
## 4.2 个性签名修改
&emsp;&emsp;用户点击个性签名，会进入到个性签名修改界面，改正后点击确认即可。

## 4.3 作者博客
&emsp;&emsp;用户点击作者博客，可以进入到该APP开发者的个人CSDN博客界面，在这里用户可以留言或者私信开发者，反馈使用该APP过程中所遇到的一些问题。

## 4.4 退出登录
&emsp;&emsp;用户点击退出登录按钮，会弹出提示框，询问是否确认退出，点击确认后，将清除该用户的所有相关信息，然后跳转到登录界面。
