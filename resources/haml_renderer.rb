require 'haml'

$: << File.dirname(__FILE__)

class HamlRenderer
  def initialize(files, options)
    @input  = files
    @outdir = options[:outdir]
    @ugly   = options[:ugly]
  end

  def spit(file, contents)
    File.open(file, "w") do |f|
      f.write(contents)
    end
  end

  def extname(file)
    File.basename(file).gsub(/(\.html)?\.haml$/, ".html")
  end

  def outdir
    @outdir || ENV['BOOT_RSC_PATH']
  end

  def run!
    @input.each do |input|
      f = File.new(input)
      source = f.read
      engine = Haml::Engine.new(source, {filename: File.basename(f), ugly: @ugly})
      target = File.join(outdir, extname(f))
      spit(target, engine.render)
    end
  end
end

# Global vars are set in boot-haml task
HamlRenderer.new($input, outdir: $outdir, ugly: $ugly).run!
