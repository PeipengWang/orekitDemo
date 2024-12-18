package com.orekitdemo.Test01;

import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

public class Main {
    public static void main(String[] args) throws URISyntaxException {
        // 配置参考上下文
        URL resource = Main.class.getClassLoader().getResource("orekit-data-master");
        if (resource == null) {
            throw new IllegalArgumentException("orekit-data-master not found in resources directory");
        }
        // 转换为 File 对象
        File orekitData = new File(resource.toURI());
        if (!orekitData.exists()) {
            System.err.format(Locale.US, "无法找到 %s 文件夹%n",
                    orekitData.getAbsolutePath());
            System.err.format(Locale.US, "您需要从 %s 下载 %s，解压缩到 %s 并将其重命名为 'orekit-data'，以使本教程正常工作%n",
                    "https://gitlab.orekit.org/orekit/orekit-data/-/archive/master/orekit-data-master.zip",
                    "orekit-data-master.zip",
                    orekitData.getAbsolutePath());
            System.exit(1);
        }
        DataContext.getDefault().
                getDataProvidersManager().
                addProvider(new DirectoryCrawler(orekitData));
        // 时间初始化
        AbsoluteDate initialDate = new AbsoluteDate(2024, 12, 18, 12, 0, 0.0, TimeScalesFactory.getUTC());

        // 地球参考系
        Frame inertialFrame = FramesFactory.getEME2000();

        // 初始化轨道
        double a = 7000000.0; // 半长轴，单位：米
        double e = 0.01;      // 偏心率
        double i = Math.toRadians(98.0); // 倾角，单位：弧度
        double omega = Math.toRadians(0.0);  // 升交点赤经
        double raan = Math.toRadians(45.0);  // 近地点幅角
        double lv = Math.toRadians(0.0);     // 平近点角

        Orbit orbit = new KeplerianOrbit(a, e, i, omega, raan, lv, PositionAngle.TRUE, inertialFrame, initialDate, Constants.WGS84_EARTH_MU);

        // 轨道传播器
        Propagator propagator = new KeplerianPropagator(orbit);

        // 推进到指定时间
        AbsoluteDate targetDate = initialDate.shiftedBy(3600.0); // 3600秒后
        SpacecraftState state = propagator.propagate(targetDate);

        // 输出卫星位置
        System.out.println("Position: " + state.getPVCoordinates().getPosition());

    }
}
