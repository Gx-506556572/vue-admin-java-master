1.使用了SQLite数据库，开发过程中将sqlTools.db放到项目根目录下，对应yml配置 url: jdbc:sqlite:sqlTools.db  但是在打包时，需要将sqlTools.db放到resource文件夹下，并修改yml配置：url: jdbc:sqlite::resource:sqlTools.db
2.启动项目后，如果sqlTools.db在resource文件夹下，可能会使用target中resource的db文件，如果出现这种情况，将db放到项目根目录下并修改相应配置就可以。
