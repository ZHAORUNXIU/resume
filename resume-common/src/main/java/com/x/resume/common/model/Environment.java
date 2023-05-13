package com.x.resume.common.model;

/**
 * 运行环境
 */
public class Environment {

    private String env;

    /**
     * 是否slave节点
     */
    private boolean slave;

    private String captainSeq;

    /**
     * 设置运行环境
     *
     * @param env 取值：[dev, stg, prd]
     */
    public void setEnv(String env) {
        this.env = env;
    }

    /**
     * 判断是否为测试环境
     *
     * @return boolean
     */
    public boolean isDev() {
        return env.startsWith("dev") || env.startsWith("test");
    }

    /**
     * 判断是否为本地环境
     *
     * @return boolean
     */
    public boolean isLocal() {
        return "dev-local".equals(env) || "local".equals(env);
    }

    /**
     * 判断是否为生产环境
     *
     * @return boolean
     */
    public boolean isPrd() {
        return env.startsWith("prd");
    }

    /**
     * 判断是否为预发环境
     *
     * @return boolean
     */
    public boolean isStg() {
        return env.startsWith("stg");
    }

    public boolean isSlave() {
        return slave;
    }

    public void setSlave(boolean slave) {
        this.slave = slave;
    }

    public String getEnv() {
        return env;
    }

    public String getCaptainSeq() {
        return captainSeq;
    }

    public void setCaptainSeq(String captainSeq) {
        this.captainSeq = captainSeq;
    }

}
