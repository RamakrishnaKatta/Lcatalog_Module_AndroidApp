package org.artoolkit.ar.base.rendering.gles20;

import android.opengl.GLES20;

/**
 * The shader program links together the vertex shader and the fragment shader and compiles them.
 * It also is responsible for binding the attributes. Attributes can be used to pass in values to the
 * shader during runtime.
 * <p/>
 * Finally it renders the given geometry.
 */
public class BaseShaderProgram extends ShaderProgram {

    public BaseShaderProgram(OpenGLShader vertexShader, OpenGLShader fragmentShader) {
        super(vertexShader, fragmentShader);
        bindAttributes();
    }

    protected void bindAttributes() {
        // Bind attributes
        GLES20.glBindAttribLocation(shaderProgramHandle, 0, OpenGLShader.positionVectorString);
    }

    @Override
    public int getProjectionMatrixHandle() {
        return GLES20.glGetUniformLocation(shaderProgramHandle, OpenGLShader.projectionMatrixString);
    }

    @Override
    public int getModelViewMatrixHandle() {
        return GLES20.glGetUniformLocation(shaderProgramHandle, OpenGLShader.modelViewMatrixString);
    }

    public int getPositionHandle() {
        return GLES20.glGetAttribLocation(shaderProgramHandle, OpenGLShader.positionVectorString);
    }

    public void render(float[] position) {
        setupShaderUsage();

        //camPosition.length * 4 bytes per float
        GLES20.glVertexAttribPointer(this.getPositionHandle(), position.length, GLES20.GL_FLOAT, false,
                position.length * 4, 0);
        GLES20.glEnableVertexAttribArray(this.getPositionHandle());
    }

}
