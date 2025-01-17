1.使用了SQLite数据库，开发过程中将sqlTools.db放到项目根目录下，对应yml配置 url: jdbc:sqlite:sqlTools.db  但是在打包时，需要将sqlTools.db放到resource文件夹下，并修改yml配置：url: jdbc:sqlite::resource:sqlTools.db


2.启动项目后，如果sqlTools.db在resource文件夹下，可能会使用target中resource的db文件，如果出现这种情况，将db放到项目根目录下并修改相应配置就可以。


3.现项目只加入了mysql、人大金仓、瀚高 数据库，对应的默认连接参数需要连接数据库后，进入database_argument表中进行修改


4.比对结果包含：表与表的结构对比、数据表数量对比、相同表记录数对比


5.要改造成前后端不分离，只需要将vue项目打包，放到resource/static文件夹下即可


6.默认账号密码：admin  admin

