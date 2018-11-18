package bid.adonis.lau.service;

import chinatelecom.feilong.scheduler.entity.Job;
import chinatelecom.feilong.scheduler.entity.JobConfig;
import chinatelecom.feilong.scheduler.entity.JobParams;
import chinatelecom.feilong.scheduler.entity.exception.SchedulerException;
import chinatelecom.feilong.scheduler.entity.plugins.*;
import chinatelecom.feilong.scheduler.entity.response.GeneralResponse;
import chinatelecom.feilong.scheduler.enumeration.LineColor;
import chinatelecom.feilong.scheduler.enumeration.SSHTimeout;
import chinatelecom.feilong.scheduler.enumeration.SchedulerType;
import chinatelecom.feilong.scheduler.service.SchedulerService;
import chinatelecom.feilong.scheduler.utils.JobPluginUtils;
import chinatelecom.feilong.scheduler.utils.JobUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 作业创建、发布、运行、调度开启关闭等
 *
 * @author: Adonis Lau
 * @date: 2018/11/14 11:20
 */
public class JobService {

    /**
     * 创建作业
     */
    public Job createJob() {

        Job job = null;
        try {
            // 创建组件
            // 创建Shell组件
            File shellDepFile = FileUtils.getFile("C:\\Users\\adonis\\Desktop\\tmp.txt");
            String shellDepFilePath = FileUtils.getFile("Users/adonis/Desktop/tmp.txt").getPath();
            BasePlugin<Shell> shell = JobPluginUtils.getShell("shell_test", "return -1", null, shellDepFilePath, null);
            // 创建Jar组件
            File jarPluginDepJar = FileUtils.getFile("D:\\Work\\电信理想\\meepo\\飞龙\\测试用例\\jar\\jar-test.jar");
            String jarPluginDepJarPath = FileUtils.getFile("/Work/电信理想/meepo/飞龙/测试用例/jar/jar-test.jar").getPath();
            BasePlugin<Jar> jar = JobPluginUtils.getJar("jar_test", jarPluginDepJarPath, "chinatelecom.feilong.meepo.webservice.WebService", "111 222 333 444", null);
            // 创建Python组件
            BasePlugin<Python> python = JobPluginUtils.getPython("python_test", "print('123')", null, (String) null);
            // 创建SSH组件
            BasePlugin<SSH> ssh = JobPluginUtils.getSSH("ssh_test", "10.4.71.25", "22", "meepo", "poker,123#", "java -version", SSHTimeout.OneHour);
            BasePlugin<SSH> ssh2 = JobPluginUtils.getSSH("ssh_test2", "10.4.71.25", "22", "meepo", "poker,123#", "cat ./dependentFiles/tmp.txt", SSHTimeout.OneHour);

            // 设置组件依赖关系
            // shell/ssh/ssh2 并列第一运行，默认设置继承于开始节点，所以不需要设置依赖节点
            // shell运行出错才能运行jar
            jar.setDependencies(shell, LineColor.red);
            // shell运行出错且jar运行成功才能运行python
            python.setDependencies(shell, LineColor.red);
            python.setDependencies(jar);

            // 设置作业参数
            JobParams jobParams = JobUtils.getJobParams("pathValue", "var date = new Date();\n" +
                    "var year = date.getFullYear();\n" +
                    "var month = date.getMonth() + 1;\n" +
                    "if(month < 10){\n" +
                    "  month = \"0\" + month;\n" +
                    "}\n" +
                    "var day = date.getDate();\n" +
                    "var hour = date.getHours();\n" +
                    "var minutes = date.getMinutes();\n" +
                    "if(minutes < 10){\n" +
                    "  minutes = \"0\" + minutes;\n" +
                    "}\n" +
                    "var pathValue = \"\" + year + month + day + hour + minutes;");

            // 设置作业调度策略
            JobConfig jobConfig = JobUtils.getJobConfig(SchedulerType.WEEKLY, 0, 0, 0, 1, 0);
            // 组合作业
            String jobName = "webServiceTest_" + System.currentTimeMillis();
            job = JobUtils.getJob(jobName, "40288df2631f723801632546c8f60321", "meepo_job", jobParams, jobConfig,
                    shell, jar, python, ssh, ssh2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 将创建完成的作业返回
        return job;
    }

    /**
     * 发布作业
     */
    public GeneralResponse publishJob(Job job) {
        GeneralResponse generalResponse = null;
        try {
            // 初始化作业发布服务
            SchedulerService.init("10.5.27.27", 80, "feilong3");
            // 调用作业发布方法，传入作业文件流发布作业
            generalResponse = SchedulerService.publishJob(job);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return generalResponse;
    }

    /**
     * 设置调度策略
     */
    public GeneralResponse setSchedulerConfig(String jobName, String projectId, JobConfig jobConfig) {
        GeneralResponse generalResponse = null;
        try {
            // 初始化作业发布服务
            SchedulerService.init("10.5.27.27", 80, "feilong3");
            // 调用设置调度策略方法，修改作业的调度策略
            generalResponse = SchedulerService.setJobPolicy(jobName, projectId, jobConfig);
        } catch (IOException | SchedulerException e) {
            e.printStackTrace();
        }
        return generalResponse;
    }

    /**
     * 删除作业
     */
    public GeneralResponse deleteJob(String jobName, String projectId) {
        GeneralResponse generalResponse = null;
        try {
            // 初始化作业发布服务
            SchedulerService.init("10.5.27.27", 80, "feilong3");
            // 调用作业删除方法删除作业
            generalResponse = SchedulerService.jobDelete(jobName, projectId);
        } catch (IOException | SchedulerException e) {
            e.printStackTrace();
        }
        return generalResponse;
    }

}