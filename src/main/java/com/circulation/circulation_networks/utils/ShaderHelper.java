package com.circulation.circulation_networks.utils;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public final class ShaderHelper {

    private ShaderHelper() {
    }

    public static int loadShader(Object vertexLoc, Object fragmentLoc, String... attribNames) {
        String vertSource = ShaderResourceCompat.readResource(vertexLoc);
        String fragSource = ShaderResourceCompat.readResource(fragmentLoc);
        if (vertSource == null || fragSource == null) return 0;

        int vert = compileShader(GL20.GL_VERTEX_SHADER, vertSource, vertexLoc.toString());
        int frag = compileShader(GL20.GL_FRAGMENT_SHADER, fragSource, fragmentLoc.toString());
        if (vert == 0 || frag == 0) {
            if (vert != 0) GL20.glDeleteShader(vert);
            if (frag != 0) GL20.glDeleteShader(frag);
            return 0;
        }

        int program = GL20.glCreateProgram();
        GL20.glAttachShader(program, vert);
        GL20.glAttachShader(program, frag);

        for (int i = 0; i < attribNames.length; i++) {
            GL20.glBindAttribLocation(program, i, attribNames[i]);
        }

        GL20.glLinkProgram(program);
        GL20.glDeleteShader(vert);
        GL20.glDeleteShader(frag);

        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetProgramInfoLog(program, 8192);
            CirculationFlowNetworks.LOGGER.error("Shader program link failed [{}+{}]: {}", vertexLoc, fragmentLoc, log);
            GL20.glDeleteProgram(program);
            return 0;
        }

        return program;
    }

    public static void deleteProgram(int program) {
        if (program != 0) {
            GL20.glDeleteProgram(program);
        }
    }

    public static int compileInline(String vertSource, String fragSource, String... attribNames) {
        int vert = compileShader(GL20.GL_VERTEX_SHADER, vertSource, "inline-vert");
        int frag = compileShader(GL20.GL_FRAGMENT_SHADER, fragSource, "inline-frag");
        if (vert == 0 || frag == 0) {
            if (vert != 0) GL20.glDeleteShader(vert);
            if (frag != 0) GL20.glDeleteShader(frag);
            return 0;
        }
        int program = GL20.glCreateProgram();
        GL20.glAttachShader(program, vert);
        GL20.glAttachShader(program, frag);
        for (int i = 0; i < attribNames.length; i++) {
            GL20.glBindAttribLocation(program, i, attribNames[i]);
        }
        GL20.glLinkProgram(program);
        GL20.glDeleteShader(vert);
        GL20.glDeleteShader(frag);
        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetProgramInfoLog(program, 8192);
            CirculationFlowNetworks.LOGGER.error("Inline shader link failed: {}", log);
            GL20.glDeleteProgram(program);
            return 0;
        }
        return program;
    }

    private static int compileShader(int type, String source, String name) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shader, 8192);
            CirculationFlowNetworks.LOGGER.error("Shader compile failed [{}]: {}", name, log);
            GL20.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }
}
