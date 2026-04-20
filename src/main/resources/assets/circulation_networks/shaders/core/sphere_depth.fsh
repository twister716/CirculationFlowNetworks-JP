#version 330

uniform sampler2D DepthSampler;

in vec4 vertexColor;

out vec4 fragColor;

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);
    float sceneDepth = texelFetch(DepthSampler, pixel, 0).r;
    float fragmentDepth = gl_FragCoord.z;
    float depthDelta = fragmentDepth - sceneDepth;
    float depthBand = clamp(fwidth(sceneDepth) * 1.5, 0.00003, 0.00016);
    float depthBias = max(depthBand * 0.4, 0.00002);

    if (sceneDepth >= 0.999999 || depthDelta <= depthBias || depthDelta >= depthBias + depthBand) {
        discard;
    }

    float fade = 1.0 - smoothstep(depthBias, depthBias + depthBand, depthDelta);
    fragColor = vec4(vertexColor.rgb, vertexColor.a * fade);
}
